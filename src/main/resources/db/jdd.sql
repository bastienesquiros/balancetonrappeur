-- ============================================================
-- JDD (Jeu De Données) — Balance Ton Rappeur
-- DONNÉES 100% FICTIVES — à des fins de développement uniquement
-- Ne jamais utiliser en production avec de vrais noms
-- ============================================================

-- Nettoyage (ordre important à cause des FK)
TRUNCATE submission_sources, submissions, withdrawal_requests,
         sources, accusations, rappers
RESTART IDENTITY CASCADE;

-- ============================================================
-- RAPPEURS (8 fictifs, statuts variés)
-- ============================================================

INSERT INTO rappers (name, status, spotify_id, created_at, updated_at) VALUES
    ('MC Fantôme',        'CONVICTED',   NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '1 month'),
    ('Lil Mirage',        'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '2 weeks'),
    ('DJ Spectre',        'CONTROVERSY', NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '3 months'),
    ('Young Néant',       'CONVICTED',   NULL, NOW() - INTERVAL '4 years',  NOW() - INTERVAL '6 months'),
    ('Krazy Void',        'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '1 week'),
    ('Le Vrai Personne',  'CONTROVERSY', NULL, NOW() - INTERVAL '6 months', NOW() - INTERVAL '5 days'),
    ('Phantom Bass',      'CONVICTED',   NULL, NOW() - INTERVAL '5 years',  NOW() - INTERVAL '2 months'),
    ('Nova Zéro',         'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '3 weeks');

-- ============================================================
-- ACCUSATIONS (2-3 par rappeur, catégories et statuts variés)
-- ============================================================

-- MC Fantôme — id 1
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (1, 'PHYSICAL_VIOLENCE', 'Altercation lors d''un concert à Fictiveville',   'CONVICTED',   '2021-06-14', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (1, 'CONTROVERSY',       'Propos polémiques tenus lors d''une interview',    'CONTROVERSY', '2022-03-08', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year');

-- Lil Mirage — id 2
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (2, 'SEXUAL_ASSAULT', 'Accusation d''agression lors d''une soirée privée',  'ONGOING', '2023-09-21', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '6 months'),
    (2, 'CONTROVERSY',    'Propos sexistes dans un freestyle non censuré',       'CONTROVERSY', '2022-05-10', NOW() - INTERVAL '3 years', NOW() - INTERVAL '2 years');

-- DJ Spectre — id 3
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (3, 'CONTROVERSY', 'Déclarations jugées discriminatoires en direct radio',   'CONTROVERSY', '2024-01-17', NOW() - INTERVAL '2 years', NOW() - INTERVAL '1 year'),
    (3, 'CONTROVERSY', 'Clash ayant dégénéré en appel à la haine en ligne',      'CONTROVERSY', '2023-07-04', NOW() - INTERVAL '2 years', NOW() - INTERVAL '1 year');

-- Young Néant — id 4
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (4, 'PHYSICAL_VIOLENCE', 'Condamnation pour coups sur un membre de son équipe', 'CONVICTED', '2020-11-03', NOW() - INTERVAL '4 years', NOW() - INTERVAL '3 years'),
    (4, 'CONTROVERSY',       'Mise en examen pour escroquerie envers des fans',      'ONGOING',   '2022-07-29', NOW() - INTERVAL '2 years', NOW() - INTERVAL '1 year'),
    (4, 'PHYSICAL_VIOLENCE', 'Rixe filmée à la sortie d''une boîte de nuit',         'CONVICTED', '2019-03-15', NOW() - INTERVAL '5 years', NOW() - INTERVAL '4 years');

-- Krazy Void — id 5
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (5, 'CONTROVERSY',    'Mise en examen pour propos haineux dans un clip officiel', 'ONGOING',   '2023-12-05', NOW() - INTERVAL '1 year',  NOW() - INTERVAL '6 months'),
    (5, 'SEXUAL_ASSAULT', 'Plainte déposée par une ancienne collaboratrice',           'ONGOING',   '2024-03-18', NOW() - INTERVAL '1 year',  NOW() - INTERVAL '3 months');

-- Le Vrai Personne — id 6
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (6, 'CONTROVERSY', 'Polémique suite à des propos tenus sur un réseau social',    'CONTROVERSY', '2025-02-11', NOW() - INTERVAL '6 months', NOW() - INTERVAL '1 month'),
    (6, 'CONTROVERSY', 'Sortie d''un son jugé discriminatoire, retiré sous pression', 'CONTROVERSY', '2024-11-20', NOW() - INTERVAL '9 months', NOW() - INTERVAL '5 months');

-- Phantom Bass — id 7
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (7, 'SEXUAL_ASSAULT',    'Condamné pour agression sexuelle sur une collaboratrice', 'CONVICTED', '2019-04-22', NOW() - INTERVAL '5 years', NOW() - INTERVAL '4 years'),
    (7, 'PHYSICAL_VIOLENCE', 'Accusation de violences conjugales — classée sans suite', 'ACQUITTED', '2021-08-14', NOW() - INTERVAL '3 years', NOW() - INTERVAL '2 years'),
    (7, 'PEDOPHILIA',        'Signalement pour envoi de contenus à mineure — enquête',  'ONGOING',   '2023-05-09', NOW() - INTERVAL '2 years', NOW() - INTERVAL '1 year');

-- Nova Zéro — id 8
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (8, 'PEDOPHILIA',     'Accusation d''envoi de contenus inappropriés à mineur', 'ONGOING',   '2024-10-30', NOW() - INTERVAL '1 year',  NOW() - INTERVAL '3 months'),
    (8, 'SEXUAL_ASSAULT', 'Témoignage public d''une victime présumée sur les réseaux', 'ONGOING', '2024-12-01', NOW() - INTERVAL '9 months', NOW() - INTERVAL '2 months');

-- ============================================================
-- SOURCES (2 par accusation minimum)
-- ============================================================

-- acc 1 — MC Fantôme / violence
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (1, 'PRESS',    'Le Fictif Quotidien — MC Fantôme condamné',          'https://example.com/art-1',  '2021-06-20', NOW(), NOW()),
    (1, 'JUDICIAL', 'Jugement TGI Fictiveville n°2021/4521',               'https://example.com/jug-1',  '2021-09-10', NOW(), NOW());

-- acc 2 — MC Fantôme / polémique
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (2, 'PRESS',        'RapActu — Retranscription de l''interview',       'https://example.com/art-2',  '2022-03-09', NOW(), NOW()),
    (2, 'SOCIAL_MEDIA', 'Capture de la déclaration publique',              'https://example.com/soc-2',  '2022-03-08', NOW(), NOW());

-- acc 3 — Lil Mirage / agression
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (3, 'PRESS',    'Fictif Soir — Plainte déposée contre Lil Mirage',     'https://example.com/art-3',  '2023-09-25', NOW(), NOW()),
    (3, 'JUDICIAL', 'Parquet — ouverture d''enquête préliminaire',         'https://example.com/jug-3',  '2023-10-02', NOW(), NOW());

-- acc 4 — Lil Mirage / polémique
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (4, 'SOCIAL_MEDIA', 'Vidéo du freestyle archivée',                    'https://example.com/soc-4',  '2022-05-10', NOW(), NOW());

-- acc 5 — DJ Spectre / discrim radio
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (5, 'PRESS',        'Radio Fictive — Transcription de l''émission',    'https://example.com/art-5',  '2024-01-17', NOW(), NOW()),
    (5, 'SOCIAL_MEDIA', 'Réaction d''une association antiracisme',         'https://example.com/soc-5',  '2024-01-18', NOW(), NOW());

-- acc 6 — DJ Spectre / haine
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (6, 'PRESS',        'Fictif Infos — Clash dégénéré en ligne',          'https://example.com/art-6',  '2023-07-05', NOW(), NOW());

-- acc 7 — Young Néant / coups
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (7, 'JUDICIAL', 'Arrêt Cour d''appel fictive — condamnation confirmée','https://example.com/jug-7',  '2021-02-17', NOW(), NOW()),
    (7, 'PRESS',    'Le Fictif — Young Néant condamné à 8 mois sursis',    'https://example.com/art-7',  '2021-02-18', NOW(), NOW());

-- acc 8 — Young Néant / escroquerie
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (8, 'PRESS',    'Fictif Éco — Mise en examen pour escroquerie',        'https://example.com/art-8',  '2022-08-01', NOW(), NOW());

-- acc 9 — Young Néant / rixe
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (9, 'PRESS',        'Fictif Soir — Rixe filmée impliquant Young Néant','https://example.com/art-9',  '2019-03-16', NOW(), NOW()),
    (9, 'SOCIAL_MEDIA', 'Vidéo diffusée avant suppression',                'https://example.com/soc-9',  '2019-03-15', NOW(), NOW()),
    (9, 'JUDICIAL',     'Procès-verbal de la préfecture fictive',           'https://example.com/jug-9',  '2019-04-01', NOW(), NOW());

-- acc 10 — Krazy Void / propos haineux
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (10, 'PRESS',    'Fictif Infos — Clip retiré des plateformes',         'https://example.com/art-10', '2023-12-07', NOW(), NOW()),
    (10, 'JUDICIAL', 'Parquet — information judiciaire ouverte',           'https://example.com/jug-10', '2024-01-15', NOW(), NOW());

-- acc 11 — Krazy Void / plainte collaboratrice
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (11, 'PRESS',        'Fictif Culture — Témoignage anonyme publié',     'https://example.com/art-11', '2024-03-20', NOW(), NOW()),
    (11, 'SOCIAL_MEDIA', 'Thread public de la plaignante',                 'https://example.com/soc-11', '2024-03-18', NOW(), NOW());

-- acc 12 — Le Vrai Personne / réseau social
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (12, 'SOCIAL_MEDIA', 'Post supprimé — archivé par un média fictif',   'https://example.com/soc-12', '2025-02-11', NOW(), NOW()),
    (12, 'PRESS',        'Fictif Culture — Chronique sur la polémique',    'https://example.com/art-12', '2025-02-13', NOW(), NOW());

-- acc 13 — Le Vrai Personne / son discriminatoire
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (13, 'PRESS',        'Fictif Rap — Retrait du son sous pression',      'https://example.com/art-13', '2024-11-22', NOW(), NOW()),
    (13, 'SOCIAL_MEDIA', 'Pétition en ligne ayant rassemblé 12k signatures','https://example.com/soc-13', '2024-11-21', NOW(), NOW());

-- acc 14 — Phantom Bass / agression sexuelle
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (14, 'JUDICIAL', 'Jugement TGI Fictiveville n°2019/1102',              'https://example.com/jug-14', '2020-03-05', NOW(), NOW()),
    (14, 'PRESS',    'Le Fictif — Phantom Bass condamné à 2 ans ferme',    'https://example.com/art-14', '2020-03-06', NOW(), NOW());

-- acc 15 — Phantom Bass / violences conjugales (acquitté)
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (15, 'PRESS',    'Fictif Soir — Phantom Bass acquitté',                'https://example.com/art-15', '2022-01-20', NOW(), NOW()),
    (15, 'JUDICIAL', 'Ordonnance de non-lieu — TGI Fictiveville',          'https://example.com/jug-15', '2022-01-19', NOW(), NOW());

-- acc 16 — Phantom Bass / pédophilie
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (16, 'PRESS',    'Fictif Infos — Signalement en cours d''instruction', 'https://example.com/art-16', '2023-05-10', NOW(), NOW()),
    (16, 'JUDICIAL', 'Parquet — enquête préliminaire ouverte',             'https://example.com/jug-16', '2023-05-11', NOW(), NOW());

-- acc 17 — Nova Zéro / pédophilie
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (17, 'PRESS',    'Fictif Infos — Nova Zéro mis en cause',              'https://example.com/art-17', '2024-11-02', NOW(), NOW()),
    (17, 'JUDICIAL', 'Parquet — garde à vue et audition libre',            'https://example.com/jug-17', '2024-11-01', NOW(), NOW());

-- acc 18 — Nova Zéro / témoignage
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (18, 'SOCIAL_MEDIA', 'Thread de témoignage public viral',              'https://example.com/soc-18', '2024-12-01', NOW(), NOW()),
    (18, 'PRESS',        'Fictif Culture — Couverture de l''affaire',      'https://example.com/art-18', '2024-12-03', NOW(), NOW());

-- ============================================================
-- SUBMISSIONS (ADD et EDIT, rappeurs connus et inconnus)
-- ============================================================

-- S1 — ADD_ACCUSATION sur rappeur connu (MC Fantôme, id=1) — PENDING
INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at)
VALUES ('ADD_ACCUSATION', 1, NULL, NULL, 'CONTROVERSY', 'Altercation avec un photographe lors d''un festival', 'ONGOING', '2025-01-10', 'PENDING', NOW() - INTERVAL '5 days');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (1, 'PRESS',    'Fictif Live — Incident au festival',          'https://example.com/sub-s1-1'),
    (1, 'SOCIAL_MEDIA', 'Story Instagram du photographe',         'https://example.com/sub-s1-2');

-- S2 — ADD_ACCUSATION sur rappeur inconnu — PENDING
INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at)
VALUES ('ADD_ACCUSATION', NULL, 'Ghost Flow', NULL, 'SEXUAL_ASSAULT', 'Témoignage d''une ancienne collaboratrice', 'ONGOING', '2024-08-15', 'PENDING', NOW() - INTERVAL '2 days');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (2, 'SOCIAL_MEDIA', 'Thread Twitter de la victime présumée', 'https://example.com/sub-s2-1'),
    (2, 'PRESS',        'Blog indépendant — témoignage relayé',  'https://example.com/sub-s2-2');

-- S3 — EDIT_ACCUSATION (modifier accusation id=3, Lil Mirage) — PENDING
INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at)
VALUES ('EDIT_ACCUSATION', 2, NULL, 3, 'SEXUAL_ASSAULT', 'Accusation d''agression — mise à jour après audience', 'CONVICTED', '2023-09-21', 'PENDING', NOW() - INTERVAL '1 day');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (3, 'JUDICIAL', 'Jugement TGI Fictiveville — condamnation', 'https://example.com/sub-s3-1');

-- S4 — ADD_ACCUSATION sur rappeur connu (DJ Spectre, id=3) — APPROVED
INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at)
VALUES ('ADD_ACCUSATION', 3, NULL, NULL, 'CONTROVERSY', 'Déclarations polémiques dans un podcast', 'CONTROVERSY', '2023-04-12', 'APPROVED', NOW() - INTERVAL '30 days');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (4, 'PRESS', 'Fictif Podcast — Revue de presse', 'https://example.com/sub-s4-1');

-- S5 — ADD_ACCUSATION sur rappeur inconnu — REJECTED
INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at)
VALUES ('ADD_ACCUSATION', NULL, 'Fake Artist', NULL, 'PHYSICAL_VIOLENCE', 'Accusation sans source vérifiable', 'ONGOING', '2024-06-01', 'REJECTED', NOW() - INTERVAL '15 days');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (5, 'SOCIAL_MEDIA', 'Tweet anonyme non sourcé', 'https://example.com/sub-s5-1');

-- ============================================================
-- WITHDRAWAL REQUESTS
-- ============================================================

-- W1 — Demande de retrait sur accusation 2 (MC Fantôme / polémique) — PENDING avec email
INSERT INTO withdrawal_requests (rapper_name, accusation_id, accusation_title, reason, message, email, status, created_at)
VALUES (
    'MC Fantôme',
    2,
    'Propos polémiques tenus lors d''une interview',
    'INCORRECT_INFO',
    'Cette information est incorrecte — l''interview a été mal interprétée et un démenti a été publié. Je souhaite que cette fiche soit corrigée.',
    'contact@fictif-management.fr',
    'PENDING',
    NOW() - INTERVAL '3 days'
);

-- W2 — Demande de retrait sur accusation 15 (Phantom Bass / acquitté) — PENDING sans email
INSERT INTO withdrawal_requests (rapper_name, accusation_id, accusation_title, reason, message, email, status, created_at)
VALUES (
    'Phantom Bass',
    15,
    'Accusation de violences conjugales — classée sans suite',
    'OUTDATED_INFO',
    'Cette affaire a été classée sans suite et Phantom Bass a été acquitté. Maintenir cette information constitue une atteinte à sa réputation.',
    NULL,
    'PENDING',
    NOW() - INTERVAL '1 day'
);

-- W3 — Demande traitée (PROCESSED) — pour avoir de l''historique
INSERT INTO withdrawal_requests (rapper_name, accusation_id, accusation_title, reason, message, email, status, created_at)
VALUES (
    'DJ Spectre',
    6,
    'Clash ayant dégénéré en appel à la haine en ligne',
    'INCORRECT_INFO',
    'Les faits décrits sont inexacts. Demande de correction.',
    'dj-spectre@fictif.fr',
    'PROCESSED',
    NOW() - INTERVAL '20 days'
);

-- W4 — Demande rejetée (REJECTED)
INSERT INTO withdrawal_requests (rapper_name, accusation_id, accusation_title, reason, message, email, status, created_at)
VALUES (
    'Young Néant',
    8,
    'Mise en examen pour escroquerie envers des fans',
    'PRIVACY',
    'Demande de retrait pour atteinte à la vie privée.',
    NULL,
    'REJECTED',
    NOW() - INTERVAL '45 days'
);

-- ============================================================
-- Vérification rapide
-- ============================================================
SELECT '=== RAPPEURS ===' AS info;
SELECT r.name, r.status,
       COUNT(DISTINCT a.id) AS nb_accusations,
       COUNT(DISTINCT s.id) AS nb_sources
FROM rappers r
LEFT JOIN accusations a ON a.rapper_id = r.id
LEFT JOIN sources s ON s.accusation_id = a.id
GROUP BY r.id, r.name, r.status ORDER BY r.id;

SELECT '=== SUBMISSIONS ===' AS info;
SELECT sub.id, sub.type, sub.submission_status,
       COALESCE(r.name, sub.unknown_rapper_name) AS rapper,
       sub.title
FROM submissions sub
LEFT JOIN rappers r ON r.id = sub.rapper_id
ORDER BY sub.id;

SELECT '=== WITHDRAWAL REQUESTS ===' AS info;
SELECT id, rapper_name, reason, status, created_at::date FROM withdrawal_requests ORDER BY id;
