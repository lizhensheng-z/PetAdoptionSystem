package com.yr.pet.ai.service;
import cn.hutool.core.exceptions.ValidateException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.ai.cache.ChatHistoryCache;
import com.yr.pet.ai.mapper.SessionMapper;
import com.yr.pet.ai.model.entity.SessionDO;
import com.yr.pet.ai.model.vo.DaySessionVO;
import com.yr.pet.ai.model.vo.QuestionRecordVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SessionService extends ServiceImpl<SessionMapper, SessionDO> {
    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private ChatHistoryCache chatHistoryCache;

    /**
     * 创建会话
     * @param title 会话标题
     */
    public SessionDO createSession(String title) {
        //幂等校验 同一个用户 会话内对话消息不能超过十分钟 否则创建新的会话
        // 后序把sessionID存redis直接从redis取
        Long userId = UserContext.getUserId();
        if(userId==null){
            throw new ValidateException("用户未登录");
        }
        //查询最近一次会话 从redis查 查到历史消息则不创建新的会话
        Long existingSessionId = chatHistoryCache.getSessionId(userId);
        if(existingSessionId != null){
            return getById(existingSessionId);
        }
        //创建会话
        SessionDO sessionDO = new SessionDO();
        sessionDO.setTitle(title);
        sessionDO.setCreateBy(userId);
        sessionDO.setCreateTime(new Date());
        this.save(sessionDO);
        chatHistoryCache.initSession(sessionDO, userId);

        return sessionDO;
    }



    /**
     * 删除会话 ；逻辑删除
     * @Param sessionId 会话id
     */
    public void deleteSession(Long sessionId) {
        Long userId = UserContext.getUserId();
        if(userId==null){
            throw new ValidateException("用户未登录");
        }
        SessionDO sessionDO = this.lambdaQuery()
                .eq(SessionDO::getSessionId, sessionId)
                .eq(SessionDO::getCreateBy, userId)
                .one();
        if(sessionDO==null){
            throw new ValidateException("会话不存在");
        }
        sessionDO.setIfDelete(true);
        this.updateById(sessionDO);
    }


    /**
     * 获取会话详情
     * @Param sessionId 会话id
     */
    public List<QuestionRecordVO> getSessionDetail(Long sessionId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new ValidateException("用户未登录");
        }
        return sessionMapper.getQuestionRecordsBySessionId(sessionId, userId);
    }


    /**
     * 按天分组获取最近 days 天的会话列表
     * @param days 最近多少天（前端传，默认 7）
     */
    public List<DaySessionVO> queryRecentDays(Integer days) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new ValidateException("用户未登录");
        }
        List<SessionDO> sessionDOS = queryRecentDays(userId, days);
        // 2. 按天分组
        return groupToDayVO(sessionDOS);
    }
    /**
     * 把 List<SessionDO> -> List<DaySessionVO>
     */
    private List<DaySessionVO> groupToDayVO(List<SessionDO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<SessionDO>> map = list.stream()
                .collect(Collectors.groupingBy(
                        s -> DateTimeFormatter.ISO_LOCAL_DATE.format(
                                s.getCreateTime().toInstant()      // Date -> Instant
                                        .atZone(ZoneId.systemDefault()))));

        return map.entrySet()
                .stream()
                .map(e -> new DaySessionVO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DaySessionVO::getDay).reversed())
                .collect(Collectors.toList());
    }
    /**
     * @param userId 用户
     * @param days   最近多少天（前端传，默认 7）
     */
    public List<SessionDO> queryRecentDays(Long userId, Integer days){
        LocalDateTime end   = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        LambdaQueryWrapper<SessionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SessionDO::getCreateBy, userId)
                .eq(SessionDO::getIfDelete, false)
                .ge(SessionDO::getCreateTime, start)  // ≥ 起始时间
                .le(SessionDO::getCreateTime, end)    // ≤ 截止时间
                .orderByAsc(SessionDO::getCreateTime); // 按时间升序，方便取每天最早一条

        // 查询时间范围内所有会话
        List<SessionDO> allSessions = sessionMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(allSessions)) {
            return Collections.emptyList();
        }

        // 按天分组后，只保留每天最早的一条
        Map<String, SessionDO> dayFirstSessionMap = new LinkedHashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (SessionDO s : allSessions) {
            String day = dayFormatter.format(
                    s.getCreateTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            );
            // 如果这一天还没放入数据，则当前这条就是这一天最早的一条
            dayFirstSessionMap.putIfAbsent(day, s);
        }

        // 返回按日期倒序（最近的天在前）排列的 SessionDO 列表
        return dayFirstSessionMap.entrySet().stream()
                .sorted(Map.Entry.<String, SessionDO>comparingByKey().reversed())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 删除某天所有会话
     * @param date 某天日期
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDaySession(Date date) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new ValidateException("用户未登录");
        }
        LocalDateTime startOfDay = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        LambdaQueryWrapper<SessionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SessionDO::getCreateBy, userId)
                .eq(SessionDO::getIfDelete, false)
                .ge(SessionDO::getCreateTime, startOfDay)
                .le(SessionDO::getCreateTime, endOfDay);

        List<SessionDO> sessions = sessionMapper.selectList(wrapper);
        for (SessionDO session : sessions) {
            session.setIfDelete(true);
            this.updateById(session);
        }
    }
}
