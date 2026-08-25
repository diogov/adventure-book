package com.adventurebook.api.dto;

public record DecisionOptionResponse(SectionResponse section, int health, boolean dead, String consequenceText) {}
