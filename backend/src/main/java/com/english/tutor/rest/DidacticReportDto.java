package com.english.tutor.rest;

import java.util.List;

public class DidacticReportDto {
    public String summary;
    public List<String> strengths;
    public List<String> weaknesses;
    public String actionPlan;

    public DidacticReportDto() {}

    public DidacticReportDto(String summary, List<String> strengths, List<String> weaknesses, String actionPlan) {
        this.summary = summary;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.actionPlan = actionPlan;
    }
}
