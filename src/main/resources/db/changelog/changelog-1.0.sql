-- liquibase formatted sql

-- changeset leon:change1-1
CREATE TABLE "configuration_questions" ("configuration_id" UUID NOT NULL, "questions_id" UUID NOT NULL, CONSTRAINT "configuration_questions_pkey" PRIMARY KEY ("configuration_id", "questions_id"));

-- changeset leon:change1-2
ALTER TABLE "configuration_questions" ADD CONSTRAINT "uk_87jmj05cn4rqb8wfq6qxej42w" UNIQUE ("questions_id");

-- changeset leon:change1-3
CREATE TABLE "configuration" ("id" UUID NOT NULL, "name" VARCHAR(255) NOT NULL, CONSTRAINT "configuration_pkey" PRIMARY KEY ("id"));

-- changeset leon:change1-4
CREATE TABLE "question" ("id" UUID NOT NULL, "answer" VARCHAR(255) NOT NULL, "question_text" VARCHAR(255) NOT NULL, CONSTRAINT "question_pkey" PRIMARY KEY ("id"));

-- changeset leon:change1-5
ALTER TABLE "configuration_questions" ADD CONSTRAINT "fkewy22y8x7me09uka66yaovavm" FOREIGN KEY ("questions_id") REFERENCES "question" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset leon:change1-6
ALTER TABLE "configuration_questions" ADD CONSTRAINT "fkpuxg1dtbsi0no6cj8ynv0f8tt" FOREIGN KEY ("configuration_id") REFERENCES "configuration" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

