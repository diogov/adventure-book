ALTER TABLE options ADD COLUMN consequence_type VARCHAR(20);
ALTER TABLE options ADD COLUMN consequence_value INTEGER;
ALTER TABLE options ADD COLUMN consequence_text VARCHAR(1000);

UPDATE options SET consequence_type = 'LOSE_HEALTH', consequence_value = 2,
    consequence_text = 'You scrape past, losing your footing for a moment.' WHERE description = 'Cross the rope bridge';
UPDATE options SET consequence_type = 'LOSE_HEALTH', consequence_value = 12,
    consequence_text = 'The rocks give way beneath you.' WHERE description = 'Search the rocky walls';
