package com.yr.pet.ai.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.ai.model.dto.QuestionTemplateDTO;
import com.yr.pet.ai.model.dto.TemplatePageQueryDTO;
import com.yr.pet.ai.model.entity.QuestionTemplateDO;
import com.yr.pet.ai.service.QuestionTemplateService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * @author 李振生
 */
//@Api(tags = "ai问题模板服务")
@RestController
@RequestMapping("/ai/questionTemplate")
public class QuestionTemplateController {

    @Resource
    private QuestionTemplateService questionTemplateService;


    /**
     * 批量新增问题模板
     * @param createDTOList 数据列表
     */
//    @ApiOperation("批量新增问题模板")
    @PostMapping("/batchCreate")
    public void batchCreate(@RequestBody List<QuestionTemplateDTO> createDTOList) {
       questionTemplateService.batchCreate(createDTOList);

    }
//    @ApiOperation("更新问题模板")
    @PostMapping("/updateTemplate")
    public void updateTemplate(@RequestBody QuestionTemplateDTO questionTemplateDTO) {
        questionTemplateService.updateTemplate(questionTemplateDTO);
    }

//    @ApiOperation("删除问题模板")
    @PostMapping("/deleteTemplate")
    public void deleteTemplate(@RequestParam Long id) {
        questionTemplateService.deleteTemplate(id);
    }

//    @ApiOperation("获取所有模板列表")
    @GetMapping("/page")
    public Page<QuestionTemplateDO> list(TemplatePageQueryDTO param) {
        return questionTemplateService.pageQuestionTemplate(param);
    }


    /**
     * 根据id查询模板详情
     */
//    @ApiOperation("根据id查询模板详情")
    @GetMapping("/detail")
    public QuestionTemplateDO detail(@RequestParam Long id) {
        return questionTemplateService.getQuestionTemplateById(id);
    }
//    @ApiOperation("查询免责声明文本")
    @GetMapping("/getDisclaimer")
    public QuestionTemplateDO getDisclaimer(){
        return questionTemplateService.getDisclaimer();
    }


}
