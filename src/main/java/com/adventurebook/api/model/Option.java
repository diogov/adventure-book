package com.adventurebook.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "options")
@Getter
@Setter
@NoArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String description;

    @Column(name = "next_section_number", nullable = false)
    private Integer nextSectionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "consequence_type")
    private ConsequenceType consequenceType;

    @Column(name = "consequence_value")
    private Integer consequenceValue;

    @Column(name = "consequence_text")
    private String consequenceText;
}
