package com.yr.pet.adoption.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


/**
 * 领养申请请求DTO
 * @author yr
 * @since 2024-01-01
 */
public class AdoptionApplicationRequest {

    @NotNull(message = "宠物ID不能为空")
    @JsonProperty("petId")
    private Long petId;

    @NotEmpty(message = "问卷内容不能为空")
    @JsonProperty("questionnaire")
    private QuestionnaireRequest questionnaire;

    // Getters and Setters
    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public QuestionnaireRequest getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireRequest questionnaire) {
        this.questionnaire = questionnaire;
    }
}