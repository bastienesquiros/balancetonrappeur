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
-- RAPPEURS (30 fictifs — assez pour tester la pagination /rappers : 24/page)
-- ============================================================

INSERT INTO rappers (name, status, spotify_image_url, created_at, updated_at) VALUES
    -- Page 1 (ordre alpha)
    ('Alpha Noir',       'CONVICTED',   NULL, NOW() - INTERVAL '4 years',  NOW() - INTERVAL '1 month'),
    ('Baron Flow',       'ACCUSED',     NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 weeks'),
    ('Cypher Ghost',     'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '3 months'),
    ('Dark Mirage',      'CONVICTED',   NULL, NOW() - INTERVAL '5 years',  NOW() - INTERVAL '6 months'),
    ('Erwan Shade',      'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '1 week'),
    ('Fantom Blaze',     'ACCUSED',     NULL, NOW() - INTERVAL '6 months', NOW() - INTERVAL '5 days'),
    ('Ghost Rider 2.0',  'CONVICTED',   NULL, NOW() - INTERVAL '5 years',  NOW() - INTERVAL '2 months'),
    ('Hyper Void',       'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '3 weeks'),
    ('Ice Phantom',      'ACCUSED',     NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '1 month'),
    ('Jay Néant',        'CONVICTED',   NULL, NOW() - INTERVAL '4 years',  NOW() - INTERVAL '4 months'),
    ('Kira Zéro',        'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '2 weeks'),
    ('Lune Noire',       'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '5 months'),
    ('Mako Blaze',       'CONVICTED',   NULL, NOW() - INTERVAL '6 years',  NOW() - INTERVAL '8 months'),
    ('Nova Phantom',     'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '3 weeks'),
    ('Oxide MC',         'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 month'),
    ('Phantom Rx',       'CONVICTED',   NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '6 months'),
    ('Quasar Bass',      'ACCUSED',     NULL, NOW() - INTERVAL '8 months', NOW() - INTERVAL '1 week'),
    ('Radon Flow',       'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '2 months'),
    ('Shadow Ink',       'CONVICTED',   NULL, NOW() - INTERVAL '5 years',  NOW() - INTERVAL '3 months'),
    ('Titan Vibe',       'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '4 weeks'),
    ('Ultra Spectre',    'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '10 days'),
    ('Venom Track',      'CONVICTED',   NULL, NOW() - INTERVAL '4 years',  NOW() - INTERVAL '5 months'),
    ('Wraith MC',        'ACCUSED',     NULL, NOW() - INTERVAL '6 months', NOW() - INTERVAL '3 days'),
    ('Xeno Shade',       'ACCUSED',     NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '1 month'),
    -- Page 2 (rappeurs 25-30)
    ('Yako Blaze',       'CONVICTED',   NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '7 months'),
    ('Zero Phantom',     'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '2 weeks'),
    ('Abyss Flow',       'ACCUSED',     NULL, NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 months'),
    ('Blaze Éclipse',    'CONVICTED',   NULL, NOW() - INTERVAL '3 years',  NOW() - INTERVAL '1 month'),
    ('Cobalt Noir',      'ACCUSED',     NULL, NOW() - INTERVAL '2 years',  NOW() - INTERVAL '5 weeks'),
    ('Delta Void',       'ACCUSED',     NULL, NOW() - INTERVAL '1 year',   NOW() - INTERVAL '2 months');

-- ============================================================
-- ACCUSATIONS (~75 au total — assez pour tester /accusations : 20/page)
-- ============================================================

-- Alpha Noir (id=1) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (1, 'PHYSICAL_VIOLENCE', 'Condamné pour coups sur un membre de son crew',            'CONVICTED', '2021-06-14', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (1, 'DOMESTIC_VIOLENCE', 'Plainte de son ex-compagne pour violences conjugales',     'ONGOING',   '2022-09-03', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year'),
    (1, 'POLEMIC',           'Propos polémiques tenus lors d''une interview télévisée',  'ONGOING',   '2023-03-08', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '6 months');

-- Baron Flow (id=2) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (2, 'SEXUAL_ASSAULT', 'Accusation d''agression sexuelle lors d''une soirée privée', 'ONGOING',   '2023-09-21', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '6 months'),
    (2, 'HATE_SPEECH',    'Plainte pour incitation à la haine dans un freestyle',       'ONGOING',   '2022-05-10', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years');

-- Cypher Ghost (id=3) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (3, 'HATE_SPEECH',    'Déclarations discriminatoires en direct radio',               'ONGOING',   '2024-01-17', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year'),
    (3, 'POLEMIC',        'Clash ayant dégénéré en appel à la haine en ligne',           'ONGOING',   '2023-07-04', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year');

-- Dark Mirage (id=4) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (4, 'PHYSICAL_VIOLENCE', 'Condamné pour coups sur un membre de son équipe',         'CONVICTED', '2020-11-03', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years'),
    (4, 'DOMESTIC_VIOLENCE', 'Violences conjugales — enquête en cours',                 'ONGOING',   '2022-07-29', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year'),
    (4, 'PHYSICAL_VIOLENCE', 'Rixe filmée à la sortie d''une boîte de nuit',            'CONVICTED', '2019-03-15', NOW() - INTERVAL '5 years',  NOW() - INTERVAL '4 years');

-- Erwan Shade (id=5) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (5, 'HATE_SPEECH',    'Mise en examen pour propos haineux dans un clip officiel',   'ONGOING',   '2023-12-05', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '6 months'),
    (5, 'SEXUAL_ASSAULT', 'Plainte déposée par une ancienne collaboratrice',            'ONGOING',   '2024-03-18', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '3 months');

-- Fantom Blaze (id=6) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (6, 'POLEMIC',        'Polémique suite à des propos tenus sur un réseau social',    'ONGOING',   '2025-02-11', NOW() - INTERVAL '6 months', NOW() - INTERVAL '1 month'),
    (6, 'HATE_SPEECH',    'Son retiré des plateformes après signalements massifs',      'ONGOING',   '2024-11-20', NOW() - INTERVAL '9 months', NOW() - INTERVAL '5 months');

-- Ghost Rider 2.0 (id=7) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (7, 'RAPE',              'Condamné pour viol sur une collaboratrice',               'CONVICTED', '2019-04-22', NOW() - INTERVAL '5 years',  NOW() - INTERVAL '4 years'),
    (7, 'DOMESTIC_VIOLENCE', 'Accusation de violences conjugales — classée sans suite','ACQUITTED',  '2021-08-14', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (7, 'SEXUAL_ASSAULT',    'Signalement pour agression — enquête préliminaire',       'ONGOING',   '2023-05-09', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year');

-- Hyper Void (id=8) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (8, 'HOMICIDE',       'Mis en examen pour homicide involontaire après un accident', 'ONGOING',   '2024-10-30', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '3 months'),
    (8, 'SEXUAL_ASSAULT', 'Témoignage public d''une victime présumée sur les réseaux',  'ONGOING',   '2024-12-01', NOW() - INTERVAL '9 months', NOW() - INTERVAL '2 months');

-- Ice Phantom (id=9) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (9, 'PHYSICAL_VIOLENCE', 'Condamné après une altercation violente en coulisses',   'CONVICTED', '2020-08-11', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years'),
    (9, 'POLEMIC',           'Propos misogynes dans un clip viral',                    'ONGOING',   '2022-02-14', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (9, 'HATE_SPEECH',       'Enquête pour apologie de la violence envers les femmes', 'ONGOING',   '2024-06-01', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '4 months');

-- Jay Néant (id=10) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (10, 'RAPE',          'Condamné à 4 ans ferme pour viol aggravé',                  'CONVICTED', '2019-11-22', NOW() - INTERVAL '5 years',  NOW() - INTERVAL '4 years'),
    (10, 'DOMESTIC_VIOLENCE', 'Plainte pour harcèlement envers une ex-compagne',       'ACQUITTED',  '2021-04-07', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years');

-- Kira Zéro (id=11) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (11, 'SEXUAL_ASSAULT', 'Mise en examen pour agression lors d''un after',           'ONGOING',   '2024-07-19', NOW() - INTERVAL '10 months',NOW() - INTERVAL '4 months'),
    (11, 'POLEMIC',        'Paroles jugées violentes envers une minorité',              'ONGOING',   '2023-11-30', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '8 months');

-- Lune Noire (id=12) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (12, 'PHYSICAL_VIOLENCE', 'Condamné pour violences lors d''un concert',            'CONVICTED', '2021-09-05', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (12, 'HATE_SPEECH',       'Interpellé pour incitation à la violence en ligne',     'ONGOING',   '2023-04-12', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year'),
    (12, 'POLEMIC',           'Sortie de scène agressive filmée et virale',            'ONGOING',   '2024-02-28', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '6 months');

-- Mako Blaze (id=13) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (13, 'RAPE',          'Condamné à 6 ans pour viol sur une mineure',                'CONVICTED', '2018-06-30', NOW() - INTERVAL '6 years',  NOW() - INTERVAL '5 years'),
    (13, 'PHYSICAL_VIOLENCE', 'Condamné pour agression d''un journaliste',             'CONVICTED', '2020-12-10', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years');

-- Nova Phantom (id=14) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (14, 'SEXUAL_ASSAULT', 'Signalement déposé par deux témoins',                      'ONGOING',   '2025-01-15', NOW() - INTERVAL '3 months', NOW() - INTERVAL '1 month'),
    (14, 'POLEMIC',        'Déclaration controversée dans un podcast à large audience', 'ONGOING',  '2024-09-22', NOW() - INTERVAL '6 months', NOW() - INTERVAL '3 months');

-- Oxide MC (id=15) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (15, 'POLEMIC',       'Clash homophobe sur les réseaux sociaux',                   'ONGOING',   '2023-06-18', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '1 year'),
    (15, 'HATE_SPEECH',   'Plainte d''une association LGBTQ+ pour propos haineux',     'ONGOING',   '2023-08-22', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '14 months'),
    (15, 'PHYSICAL_VIOLENCE', 'Bagarre filmée dans un studio d''enregistrement',       'ACQUITTED',  '2022-01-05', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years');

-- Phantom Rx (id=16) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (16, 'DOMESTIC_VIOLENCE', 'Condamné à 18 mois pour violences sur compagne',       'CONVICTED', '2022-03-17', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (16, 'SEXUAL_ASSAULT',    'Nouvelle plainte déposée — enquête préliminaire',       'ONGOING',   '2024-11-08', NOW() - INTERVAL '5 months', NOW() - INTERVAL '2 months');

-- Quasar Bass (id=17) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (17, 'POLEMIC',       'Sortie médiatique jugée raciste par plusieurs associations', 'ONGOING',  '2025-01-20', NOW() - INTERVAL '2 months', NOW() - INTERVAL '3 weeks'),
    (17, 'HATE_SPEECH',   'Signalé à la DILCRAH pour propos antisémites en live',      'ONGOING',  '2025-02-14', NOW() - INTERVAL '1 month',  NOW() - INTERVAL '2 weeks');

-- Radon Flow (id=18) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (18, 'PHYSICAL_VIOLENCE', 'Rixe avec un fan ayant nécessité une hospitalisation',  'ONGOING',   '2024-08-03', NOW() - INTERVAL '7 months', NOW() - INTERVAL '3 months'),
    (18, 'POLEMIC',           'Interview jugée sexiste par des associations féministes','ONGOING',   '2023-12-15', NOW() - INTERVAL '1 year',   NOW() - INTERVAL '8 months'),
    (18, 'HATE_SPEECH',       'Clip retiré pour apologie implicite de la violence',    'ONGOING',   '2024-04-01', NOW() - INTERVAL '11 months',NOW() - INTERVAL '6 months');

-- Shadow Ink (id=19) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (19, 'RAPE',          'Condamné à 5 ans pour viol en réunion',                     'CONVICTED', '2019-07-14', NOW() - INTERVAL '5 years',  NOW() - INTERVAL '4 years'),
    (19, 'PHYSICAL_VIOLENCE', 'Condamné pour coups et blessures sur garde du corps',   'CONVICTED', '2021-10-20', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years');

-- Titan Vibe (id=20) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (20, 'SEXUAL_ASSAULT', 'Deux plaintes déposées par des collaboratrices',            'ONGOING',  '2023-10-05', NOW() - INTERVAL '17 months',NOW() - INTERVAL '9 months'),
    (20, 'DOMESTIC_VIOLENCE', 'Plainte pour harcèlement moral sur ex-compagne',         'ONGOING',  '2022-12-21', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '15 months');

-- Ultra Spectre (id=21) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (21, 'POLEMIC',       'Polémique après des propos sur une personnalité publique',   'ONGOING',  '2024-10-10', NOW() - INTERVAL '5 months', NOW() - INTERVAL '2 months'),
    (21, 'HATE_SPEECH',   'Post Instagram islamophobe — retiré après 24h',             'ONGOING',  '2024-11-30', NOW() - INTERVAL '4 months', NOW() - INTERVAL '1 month'),
    (21, 'PHYSICAL_VIOLENCE', 'Altercation avec un roadie en tournée — plainte',       'ONGOING',  '2025-01-07', NOW() - INTERVAL '2 months', NOW() - INTERVAL '5 weeks');

-- Venom Track (id=22) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (22, 'RAPE',          'Condamné à 3 ans dont 18 mois ferme',                       'CONVICTED', '2021-02-28', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years'),
    (22, 'SEXUAL_ASSAULT', 'Nouvelle accusation anonyme relayée par la presse',        'ONGOING',  '2024-06-11', NOW() - INTERVAL '9 months', NOW() - INTERVAL '4 months');

-- Wraith MC (id=23) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (23, 'POLEMIC',       'Propos jugés transphobes dans une battle',                   'ONGOING',  '2025-03-01', NOW() - INTERVAL '2 weeks',  NOW() - INTERVAL '1 week'),
    (23, 'HATE_SPEECH',   'Signalement en cours après publication d''un texte haineux', 'ONGOING',  '2025-03-05', NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days');

-- Xeno Shade (id=24) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (24, 'PHYSICAL_VIOLENCE', 'Condamné pour violences aggravées sur agent de sécurité','CONVICTED', '2022-05-19', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years'),
    (24, 'DOMESTIC_VIOLENCE', 'Plainte déposée par son ex-manager pour harcèlement',   'ACQUITTED',  '2020-09-14', NOW() - INTERVAL '5 years',  NOW() - INTERVAL '4 years'),
    (24, 'POLEMIC',           'Vidéo controversée dans laquelle il banalise la violence','ONGOING',  '2024-07-07', NOW() - INTERVAL '8 months', NOW() - INTERVAL '4 months');

-- Yako Blaze (id=25) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (25, 'RAPE',              'Condamné à 7 ans pour viol et séquestration',            'CONVICTED', '2018-03-12', NOW() - INTERVAL '7 years',  NOW() - INTERVAL '6 years'),
    (25, 'PHYSICAL_VIOLENCE', 'Condamné pour violence sur ex-collaborateur',            'CONVICTED', '2022-10-04', NOW() - INTERVAL '3 years',  NOW() - INTERVAL '2 years');

-- Zero Phantom (id=26) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (26, 'SEXUAL_ASSAULT', 'Plainte déposée par une fan rencontrée en backstage',      'ONGOING',   '2024-09-18', NOW() - INTERVAL '6 months', NOW() - INTERVAL '3 months'),
    (26, 'POLEMIC',        'Freestyle jugé antisémite — retrait des plateformes',      'ONGOING',   '2023-05-22', NOW() - INTERVAL '2 years',  NOW() - INTERVAL '18 months');

-- Abyss Flow (id=27) — 3 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (27, 'DOMESTIC_VIOLENCE', 'Condamné pour violences répétées sur compagne',         'CONVICTED', '2020-06-30', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years'),
    (27, 'PHYSICAL_VIOLENCE', 'Altercation avec journaliste — plainte déposée',        'ONGOING',   '2023-09-11', NOW() - INTERVAL '18 months',NOW() - INTERVAL '10 months'),
    (27, 'POLEMIC',           'Déclarations jugées misogynes dans un entretien',        'ONGOING',  '2024-03-25', NOW() - INTERVAL '11 months',NOW() - INTERVAL '6 months');

-- Blaze Éclipse (id=28) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (28, 'RAPE',          'Condamné à 8 ans pour viol sur une mineure',                'CONVICTED', '2017-11-09', NOW() - INTERVAL '7 years',  NOW() - INTERVAL '6 years'),
    (28, 'SEXUAL_ASSAULT','Deuxième affaire instruite par un juge d''instruction',      'ONGOING',   '2024-05-03', NOW() - INTERVAL '10 months',NOW() - INTERVAL '5 months');

-- Cobalt Noir (id=29) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (29, 'HATE_SPEECH',   'Condamné pour provocation à la haine raciale',              'CONVICTED', '2021-12-18', NOW() - INTERVAL '4 years',  NOW() - INTERVAL '3 years'),
    (29, 'POLEMIC',       'Prise de position controversée sur un sujet politique',     'ONGOING',   '2024-08-30', NOW() - INTERVAL '6 months', NOW() - INTERVAL '3 months');

-- Delta Void (id=30) — 2 accusations
INSERT INTO accusations (rapper_id, category, title, status, fact_date, created_at, updated_at) VALUES
    (30, 'SEXUAL_ASSAULT','Signalement par deux témoins — enquête préliminaire',       'ONGOING',   '2025-01-28', NOW() - INTERVAL '5 weeks',  NOW() - INTERVAL '2 weeks'),
    (30, 'POLEMIC',       'Clash haineux contre un autre artiste — dénoncé publiquement','ONGOING', '2024-12-10', NOW() - INTERVAL '3 months', NOW() - INTERVAL '1 month');

-- ============================================================
-- SOURCES (au moins 1 par accusation)
-- ============================================================

-- Alpha Noir acc 1, 2, 3
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (1, 'PRESS',    'Fictif Quotidien — Alpha Noir condamné',           'https://example.com/s1-1',  '2021-06-20', NOW(), NOW()),
    (1, 'JUDICIAL', 'Jugement TGI Fictiveville n°2021/4521',            'https://example.com/s1-2',  '2021-09-10', NOW(), NOW()),
    (2, 'PRESS',    'Fictif Soir — Plainte contre Alpha Noir',          'https://example.com/s2-1',  '2022-09-05', NOW(), NOW()),
    (3, 'SOCIAL_MEDIA', 'Capture de l''interview virale',               'https://example.com/s3-1',  '2023-03-09', NOW(), NOW()),
    (3, 'PRESS',    'RapActu — Retranscription complète',               'https://example.com/s3-2',  '2023-03-10', NOW(), NOW());

-- Baron Flow acc 4, 5
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (4, 'PRESS',    'Fictif Soir — Plainte déposée contre Baron Flow',  'https://example.com/s4-1',  '2023-09-25', NOW(), NOW()),
    (4, 'JUDICIAL', 'Parquet — ouverture enquête préliminaire',         'https://example.com/s4-2',  '2023-10-02', NOW(), NOW()),
    (5, 'SOCIAL_MEDIA', 'Vidéo du freestyle archivée',                  'https://example.com/s5-1',  '2022-05-10', NOW(), NOW());

-- Cypher Ghost acc 6, 7
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (6, 'PRESS',    'Radio Fictive — Transcription émission',           'https://example.com/s6-1',  '2024-01-17', NOW(), NOW()),
    (6, 'SOCIAL_MEDIA', 'Réaction association antiracisme',             'https://example.com/s6-2',  '2024-01-18', NOW(), NOW()),
    (7, 'PRESS',    'Fictif Infos — Clash dégénéré en ligne',           'https://example.com/s7-1',  '2023-07-05', NOW(), NOW());

-- Dark Mirage acc 8, 9, 10
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (8, 'JUDICIAL', 'Arrêt Cour d''appel — condamnation confirmée',    'https://example.com/s8-1',  '2021-02-17', NOW(), NOW()),
    (8, 'PRESS',    'Le Fictif — Dark Mirage condamné à 8 mois',       'https://example.com/s8-2',  '2021-02-18', NOW(), NOW()),
    (9, 'PRESS',    'Fictif Éco — Enquête en cours',                    'https://example.com/s9-1',  '2022-08-01', NOW(), NOW()),
    (10, 'PRESS',   'Fictif Soir — Rixe filmée',                       'https://example.com/s10-1', '2019-03-16', NOW(), NOW()),
    (10, 'SOCIAL_MEDIA', 'Vidéo diffusée avant suppression',           'https://example.com/s10-2', '2019-03-15', NOW(), NOW());

-- Erwan Shade acc 11, 12
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (11, 'PRESS',   'Fictif Infos — Clip retiré des plateformes',      'https://example.com/s11-1', '2023-12-07', NOW(), NOW()),
    (11, 'JUDICIAL','Parquet — information judiciaire ouverte',         'https://example.com/s11-2', '2024-01-15', NOW(), NOW()),
    (12, 'PRESS',   'Fictif Culture — Témoignage anonyme publié',      'https://example.com/s12-1', '2024-03-20', NOW(), NOW()),
    (12, 'SOCIAL_MEDIA', 'Thread public de la plaignante',             'https://example.com/s12-2', '2024-03-18', NOW(), NOW());

-- Fantom Blaze acc 13, 14
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (13, 'SOCIAL_MEDIA', 'Post supprimé — archivé par un média fictif','https://example.com/s13-1', '2025-02-11', NOW(), NOW()),
    (13, 'PRESS',  'Fictif Culture — Chronique sur la polémique',      'https://example.com/s13-2', '2025-02-13', NOW(), NOW()),
    (14, 'PRESS',  'Fictif Rap — Retrait du son sous pression',        'https://example.com/s14-1', '2024-11-22', NOW(), NOW());

-- Ghost Rider acc 15, 16, 17
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (15, 'JUDICIAL','Jugement TGI Fictiveville n°2019/1102',            'https://example.com/s15-1', '2020-03-05', NOW(), NOW()),
    (15, 'PRESS',   'Le Fictif — Ghost Rider condamné à 2 ans ferme',  'https://example.com/s15-2', '2020-03-06', NOW(), NOW()),
    (16, 'PRESS',   'Fictif Soir — Acquitté',                          'https://example.com/s16-1', '2022-01-20', NOW(), NOW()),
    (17, 'PRESS',   'Fictif Infos — Enquête préliminaire',             'https://example.com/s17-1', '2023-05-10', NOW(), NOW());

-- Hyper Void acc 18, 19
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (18, 'PRESS',   'Fictif Infos — Hyper Void mis en cause',          'https://example.com/s18-1', '2024-11-02', NOW(), NOW()),
    (18, 'JUDICIAL','Parquet — garde à vue et audition libre',          'https://example.com/s18-2', '2024-11-01', NOW(), NOW()),
    (19, 'SOCIAL_MEDIA', 'Thread de témoignage public viral',          'https://example.com/s19-1', '2024-12-01', NOW(), NOW()),
    (19, 'PRESS',   'Fictif Culture — Couverture de l''affaire',       'https://example.com/s19-2', '2024-12-03', NOW(), NOW());

-- Ice Phantom acc 20, 21, 22
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (20, 'JUDICIAL','Jugement correctionnel — 6 mois ferme',           'https://example.com/s20-1', '2020-10-01', NOW(), NOW()),
    (21, 'SOCIAL_MEDIA', 'Captures du clip avant retrait',             'https://example.com/s21-1', '2022-02-15', NOW(), NOW()),
    (22, 'PRESS',   'Fictif Soir — Enquête ouverte',                   'https://example.com/s22-1', '2024-06-03', NOW(), NOW()),
    (22, 'JUDICIAL','Parquet — convocation de Ice Phantom',            'https://example.com/s22-2', '2024-06-10', NOW(), NOW());

-- Jay Néant acc 23, 24
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (23, 'JUDICIAL','Arrêt Cour d''assises — 4 ans ferme',             'https://example.com/s23-1', '2020-06-14', NOW(), NOW()),
    (23, 'PRESS',   'Le Fictif — Jay Néant condamné',                  'https://example.com/s23-2', '2020-06-15', NOW(), NOW()),
    (24, 'JUDICIAL','Classement sans suite — TGI',                     'https://example.com/s24-1', '2022-07-01', NOW(), NOW());

-- Kira Zéro acc 25, 26
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (25, 'PRESS',   'Fictif Soir — Kira Zéro en garde à vue',         'https://example.com/s25-1', '2024-07-20', NOW(), NOW()),
    (26, 'SOCIAL_MEDIA', 'Réactions de communautés en ligne',         'https://example.com/s26-1', '2023-12-01', NOW(), NOW());

-- Lune Noire acc 27, 28, 29
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (27, 'JUDICIAL','Jugement correctionnel confirmé en appel',        'https://example.com/s27-1', '2022-02-14', NOW(), NOW()),
    (28, 'PRESS',   'Fictif Infos — Interpellation de Lune Noire',    'https://example.com/s28-1', '2023-04-13', NOW(), NOW()),
    (29, 'SOCIAL_MEDIA', 'Vidéo de sortie de scène archivée',         'https://example.com/s29-1', '2024-02-29', NOW(), NOW());

-- Mako Blaze acc 30, 31
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (30, 'JUDICIAL','Arrêt Cour d''assises — 6 ans ferme',            'https://example.com/s30-1', '2019-03-10', NOW(), NOW()),
    (30, 'PRESS',   'Le Fictif — Condamné pour viol sur mineure',     'https://example.com/s30-2', '2019-03-11', NOW(), NOW()),
    (31, 'JUDICIAL','Jugement correctionnel — 1 an ferme',            'https://example.com/s31-1', '2021-05-20', NOW(), NOW());

-- Nova Phantom acc 32, 33
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (32, 'PRESS',   'Fictif Culture — Deux signalements contre Nova Phantom', 'https://example.com/s32-1', '2025-01-17', NOW(), NOW()),
    (33, 'SOCIAL_MEDIA', 'Extrait audio du podcast controversé',      'https://example.com/s33-1', '2024-09-23', NOW(), NOW());

-- Oxide MC acc 34, 35, 36
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (34, 'SOCIAL_MEDIA', 'Capture du post homophobe',                 'https://example.com/s34-1', '2023-06-18', NOW(), NOW()),
    (35, 'PRESS',   'Association LGBTQ+ — communiqué officiel',       'https://example.com/s35-1', '2023-08-23', NOW(), NOW()),
    (35, 'JUDICIAL','Parquet — dépôt de plainte enregistré',          'https://example.com/s35-2', '2023-09-01', NOW(), NOW()),
    (36, 'PRESS',   'Fictif Rap — Bagarre en studio',                 'https://example.com/s36-1', '2022-01-06', NOW(), NOW());

-- Phantom Rx acc 37, 38
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (37, 'JUDICIAL','Jugement TGI — 18 mois pour violences conjugales','https://example.com/s37-1', '2022-09-14', NOW(), NOW()),
    (37, 'PRESS',   'Fictif Soir — Condamné',                         'https://example.com/s37-2', '2022-09-15', NOW(), NOW()),
    (38, 'PRESS',   'Fictif Infos — Nouvelle plainte',                'https://example.com/s38-1', '2024-11-09', NOW(), NOW());

-- Quasar Bass acc 39, 40
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (39, 'SOCIAL_MEDIA', 'Extrait de l''interview virale',            'https://example.com/s39-1', '2025-01-20', NOW(), NOW()),
    (39, 'PRESS',  'Fictif Actu — Réactions en chaîne',               'https://example.com/s39-2', '2025-01-21', NOW(), NOW()),
    (40, 'PRESS',  'Fictif Infos — Signalement à la DILCRAH',         'https://example.com/s40-1', '2025-02-15', NOW(), NOW()),
    (40, 'JUDICIAL','Parquet — enquête préliminaire ouverte',         'https://example.com/s40-2', '2025-02-20', NOW(), NOW());

-- Radon Flow acc 41, 42, 43
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (41, 'PRESS',  'Fictif Soir — Hospitalisation du fan',            'https://example.com/s41-1', '2024-08-04', NOW(), NOW()),
    (42, 'SOCIAL_MEDIA', 'Extrait de l''interview sexiste',           'https://example.com/s42-1', '2023-12-15', NOW(), NOW()),
    (43, 'PRESS',  'Fictif Rap — Clip controversé retiré',            'https://example.com/s43-1', '2024-04-02', NOW(), NOW());

-- Shadow Ink acc 44, 45
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (44, 'JUDICIAL','Arrêt Cour d''assises — 5 ans ferme',            'https://example.com/s44-1', '2020-01-22', NOW(), NOW()),
    (44, 'PRESS',  'Le Fictif — Shadow Ink condamné',                 'https://example.com/s44-2', '2020-01-23', NOW(), NOW()),
    (45, 'JUDICIAL','Jugement correctionnel — coups et blessures',    'https://example.com/s45-1', '2022-03-15', NOW(), NOW());

-- Titan Vibe acc 46, 47
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (46, 'PRESS',  'Fictif Culture — Deux plaintes contre Titan Vibe','https://example.com/s46-1', '2023-10-07', NOW(), NOW()),
    (47, 'SOCIAL_MEDIA', 'Témoignage de l''ex-compagne rendu public', 'https://example.com/s47-1', '2022-12-22', NOW(), NOW());

-- Ultra Spectre acc 48, 49, 50
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (48, 'PRESS',  'Fictif Actu — Polémique autour d''Ultra Spectre',  'https://example.com/s48-1', '2024-10-11', NOW(), NOW()),
    (49, 'SOCIAL_MEDIA', 'Capture du post islamophobe',               'https://example.com/s49-1', '2024-11-30', NOW(), NOW()),
    (50, 'PRESS',  'Fictif Live — Altercation en tournée',            'https://example.com/s50-1', '2025-01-08', NOW(), NOW()),
    (50, 'JUDICIAL','Plainte déposée — convocation TGI',              'https://example.com/s50-2', '2025-01-20', NOW(), NOW());

-- Venom Track acc 51, 52
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (51, 'JUDICIAL','Jugement — 18 mois ferme',                       'https://example.com/s51-1', '2021-10-14', NOW(), NOW()),
    (51, 'PRESS',  'Le Fictif — Venom Track condamné',                'https://example.com/s51-2', '2021-10-15', NOW(), NOW()),
    (52, 'PRESS',  'Fictif Culture — Nouvelle accusation anonyme',    'https://example.com/s52-1', '2024-06-12', NOW(), NOW());

-- Wraith MC acc 53, 54
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (53, 'SOCIAL_MEDIA', 'Extrait de la battle archivée',             'https://example.com/s53-1', '2025-03-01', NOW(), NOW()),
    (54, 'PRESS',  'Fictif Infos — Signalement en cours',             'https://example.com/s54-1', '2025-03-06', NOW(), NOW()),
    (54, 'JUDICIAL','Parquet — dépôt de plainte',                     'https://example.com/s54-2', '2025-03-07', NOW(), NOW());

-- Xeno Shade acc 55, 56, 57
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (55, 'JUDICIAL','Jugement correctionnel — 6 mois ferme',          'https://example.com/s55-1', '2022-11-04', NOW(), NOW()),
    (55, 'PRESS',  'Le Fictif — Xeno Shade condamné',                 'https://example.com/s55-2', '2022-11-05', NOW(), NOW()),
    (56, 'JUDICIAL','Ordonnance de non-lieu',                         'https://example.com/s56-1', '2021-03-20', NOW(), NOW()),
    (57, 'SOCIAL_MEDIA', 'Vidéo controversée',                        'https://example.com/s57-1', '2024-07-08', NOW(), NOW());

-- Yako Blaze acc 58, 59
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (58, 'JUDICIAL','Arrêt Cour d''assises — 7 ans',                  'https://example.com/s58-1', '2018-12-05', NOW(), NOW()),
    (58, 'PRESS',  'Le Fictif — Yako Blaze condamné à 7 ans',         'https://example.com/s58-2', '2018-12-06', NOW(), NOW()),
    (59, 'JUDICIAL','Jugement correctionnel — 4 mois ferme',          'https://example.com/s59-1', '2023-03-22', NOW(), NOW());

-- Zero Phantom acc 60, 61
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (60, 'PRESS',  'Fictif Infos — Plainte en backstage',             'https://example.com/s60-1', '2024-09-19', NOW(), NOW()),
    (61, 'SOCIAL_MEDIA', 'Extrait du freestyle controversé',          'https://example.com/s61-1', '2023-05-23', NOW(), NOW()),
    (61, 'PRESS',  'Fictif Culture — Retrait des plateformes',        'https://example.com/s61-2', '2023-05-25', NOW(), NOW());

-- Abyss Flow acc 62, 63, 64
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (62, 'JUDICIAL','Jugement — violences conjugales habituelles',     'https://example.com/s62-1', '2021-01-14', NOW(), NOW()),
    (62, 'PRESS',  'Le Fictif — Condamné pour violences',             'https://example.com/s62-2', '2021-01-15', NOW(), NOW()),
    (63, 'PRESS',  'Fictif Infos — Altercation avec journaliste',     'https://example.com/s63-1', '2023-09-12', NOW(), NOW()),
    (64, 'SOCIAL_MEDIA', 'Extrait de l''entretien controversé',       'https://example.com/s64-1', '2024-03-26', NOW(), NOW());

-- Blaze Éclipse acc 65, 66
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (65, 'JUDICIAL','Arrêt Cour d''assises — 8 ans',                  'https://example.com/s65-1', '2018-06-20', NOW(), NOW()),
    (65, 'PRESS',  'Le Fictif — Blaze Éclipse condamné à 8 ans',     'https://example.com/s65-2', '2018-06-21', NOW(), NOW()),
    (66, 'JUDICIAL','Juge d''instruction saisi',                       'https://example.com/s66-1', '2024-05-10', NOW(), NOW()),
    (66, 'PRESS',  'Fictif Culture — Deuxième affaire instruite',     'https://example.com/s66-2', '2024-05-12', NOW(), NOW());

-- Cobalt Noir acc 67, 68
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (67, 'JUDICIAL','Condamnation pour provocation à la haine',       'https://example.com/s67-1', '2022-05-30', NOW(), NOW()),
    (67, 'PRESS',  'Le Fictif — Cobalt Noir condamné',                'https://example.com/s67-2', '2022-05-31', NOW(), NOW()),
    (68, 'SOCIAL_MEDIA', 'Post controversé capturé',                  'https://example.com/s68-1', '2024-08-30', NOW(), NOW());

-- Delta Void acc 69, 70
INSERT INTO sources (accusation_id, type, title, url, source_date, created_at, updated_at) VALUES
    (69, 'PRESS',  'Fictif Soir — Enquête préliminaire ouverte',      'https://example.com/s69-1', '2025-01-29', NOW(), NOW()),
    (70, 'SOCIAL_MEDIA', 'Extrait du clash haineux',                  'https://example.com/s70-1', '2024-12-11', NOW(), NOW()),
    (70, 'PRESS',  'Fictif Culture — Dénonciation publique',          'https://example.com/s70-2', '2024-12-12', NOW(), NOW());

-- ============================================================
-- SUBMISSIONS (variées — PENDING pour tester l'admin)
-- ============================================================

INSERT INTO submissions (type, rapper_id, unknown_rapper_name, accusation_id, category, title, status, fact_date, submission_status, created_at) VALUES
    ('ADD_ACCUSATION', 1, NULL, NULL, 'POLEMIC',        'Altercation avec un photographe lors d''un festival', 'ONGOING', '2025-01-10', 'PENDING',  NOW() - INTERVAL '5 days'),
    ('ADD_ACCUSATION', NULL, 'Ghost Flow', NULL, 'SEXUAL_ASSAULT', 'Témoignage d''une ancienne collaboratrice',  'ONGOING', '2024-08-15', 'PENDING',  NOW() - INTERVAL '2 days'),
    ('EDIT_ACCUSATION', 2, NULL, 4, 'SEXUAL_ASSAULT',  'Accusation d''agression — mise à jour après audience', 'CONVICTED', '2023-09-21', 'PENDING', NOW() - INTERVAL '1 day'),
    ('ADD_ACCUSATION', 3, NULL, NULL, 'POLEMIC',        'Déclarations polémiques dans un podcast',              'ONGOING', '2023-04-12', 'APPROVED', NOW() - INTERVAL '30 days'),
    ('ADD_ACCUSATION', NULL, 'Fake Artist', NULL, 'PHYSICAL_VIOLENCE', 'Accusation sans source vérifiable',    'ONGOING', '2024-06-01', 'REJECTED', NOW() - INTERVAL '15 days');

INSERT INTO submission_sources (submission_id, type, title, url) VALUES
    (1, 'PRESS',       'Fictif Live — Incident au festival',         'https://example.com/sub-s1-1'),
    (1, 'SOCIAL_MEDIA','Story Instagram du photographe',             'https://example.com/sub-s1-2'),
    (2, 'SOCIAL_MEDIA','Thread Twitter de la victime présumée',      'https://example.com/sub-s2-1'),
    (2, 'PRESS',       'Blog indépendant — témoignage relayé',       'https://example.com/sub-s2-2'),
    (3, 'JUDICIAL',    'Jugement TGI Fictiveville — condamnation',   'https://example.com/sub-s3-1'),
    (4, 'PRESS',       'Fictif Podcast — Revue de presse',           'https://example.com/sub-s4-1'),
    (5, 'SOCIAL_MEDIA','Tweet anonyme non sourcé',                   'https://example.com/sub-s5-1');

-- ============================================================
-- WITHDRAWAL REQUESTS
-- ============================================================

INSERT INTO withdrawal_requests (rapper_name, accusation_id, accusation_title, reason, message, email, status, created_at) VALUES
    ('Alpha Noir',  2, 'Plainte de son ex-compagne pour violences conjugales',
     'INCORRECT_INFO', 'Information incorrecte — l''affaire a été classée sans suite depuis.', 'contact@fictif-mgmt.fr', 'PENDING',   NOW() - INTERVAL '3 days'),
    ('Ghost Rider 2.0',  16, 'Accusation de violences conjugales — classée sans suite',
     'OUTDATED_INFO',  'Affaire classée sans suite. Phantom Bass a été acquitté. Maintenir cette fiche est diffamatoire.', NULL, 'PENDING', NOW() - INTERVAL '1 day'),
    ('Cypher Ghost',  7, 'Clash ayant dégénéré en appel à la haine en ligne',
     'INCORRECT_INFO', 'Les faits décrits sont inexacts. Demande de correction.', 'cypher@fictif.fr', 'PROCESSED', NOW() - INTERVAL '20 days'),
    ('Dark Mirage',  9, 'Violences conjugales — enquête en cours',
     'PRIVACY', 'Demande de retrait pour atteinte à la vie privée.', NULL, 'REJECTED', NOW() - INTERVAL '45 days');

-- ============================================================
-- Vérification rapide
-- ============================================================
SELECT '=== RAPPEURS ===' AS info;
SELECT COUNT(*) AS total_rappers FROM rappers;

SELECT '=== ACCUSATIONS ===' AS info;
SELECT COUNT(*) AS total_accusations FROM accusations;

SELECT '=== SOURCES ===' AS info;
SELECT COUNT(*) AS total_sources FROM sources;

SELECT '=== SUBMISSIONS ===' AS info;
SELECT COUNT(*) AS total_submissions FROM submissions;

SELECT '=== WITHDRAWAL REQUESTS ===' AS info;
SELECT COUNT(*) AS total_withdrawals FROM withdrawal_requests;
