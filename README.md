# Balance Ton Rappeur

Base de données collaborative recensant des affaires, accusations et condamnations liées au monde du rap, documentées et sourcées.

> Les informations présentées proviennent de sources publiques (presse, décisions de justice, réseaux sociaux).  
> Ce site a une vocation documentaire et ne formule aucune accusation.

---

## Stack

- **Backend** — Spring Boot 4 / Java 21 / JPA + Hibernate
- **Frontend** — Thymeleaf + Tailwind CSS + Alpine.js
- **Base de données** — PostgreSQL
- **Intégration** — Spotify API (photo des artistes + scan de bibliothèque)
- **Mail** — SMTP (Resend en prod, Mailpit en dev)
- **Hébergement** — Docker + Nginx reverse proxy + Cloudflare

---

## Fonctionnalités

- Fiches rappeurs avec timeline des affaires
- Système de classification (agression sexuelle, violence physique, violences conjugales, escroquerie, propos discriminatoires, polémique)
- Statuts juridiques : Polémique · En cours · Condamné · Relaxé
- Recherche avec autocomplétion
- Soumission communautaire (modération obligatoire avant publication)
- Demande de retrait / correction
- Page statistiques
- Timeline globale des affaires
- **Scan Spotify** — analyse les liked songs et playlists, détecte les rappeurs concernés et permet de les retirer directement
- Dashboard admin (`/admin`) — gestion des soumissions, retraits, sync Spotify
- Digest mail quotidien (8h) avec les soumissions en attente
- Notifications mail aux contributeurs (confirmation, acceptation, refus)

---

## Lancer en local

**Prérequis** : Java 21, PostgreSQL, Maven

```bash
# Copier et adapter les properties de dev
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
# Adapter les valeurs (BDD locale, Spotify...)

# Lancer
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Charger les données fictives (optionnel) :
```sql
-- Dans psql
\i src/main/resources/db/jdd.sql
```

---

## Déployer avec Docker

```bash
# 1. Créer le fichier d'environnement
cp .env.example .env
nano .env

# 2. Créer le réseau proxy partagé (si pas déjà fait)
docker network create proxy

# 3. Build et démarrage
docker compose up -d --build
```

L'app tourne sur `btr_app:8080`, sans port exposé publiquement — à proxifier via Nginx/ARR.

---

## Variables d'environnement

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL |
| `DB_USERNAME` | Utilisateur BDD |
| `DB_PASSWORD` | Mot de passe BDD |
| `MAIL_HOST` | Hôte SMTP |
| `MAIL_PORT` | Port SMTP (587 pour Resend) |
| `MAIL_USERNAME` | Utilisateur SMTP |
| `MAIL_PASSWORD` | Mot de passe SMTP |
| `BTR_MAIL_FROM` | Adresse expéditeur du digest admin |
| `BTR_MAIL_NOREPLY` | Adresse expéditeur des mails transactionnels (confirmations, refus) — fallback sur `BTR_MAIL_FROM` si absent |
| `BTR_MAIL_DIGEST_TO` | Destinataire du digest quotidien (toi) |
| `BTR_ADMIN_PASSWORD` | Mot de passe dashboard `/admin` |
| `SPOTIFY_CLIENT_ID` | Client ID Spotify (optionnel — feature scan) |
| `SPOTIFY_CLIENT_SECRET` | Client Secret Spotify (optionnel — feature scan) |
| `SPOTIFY_REDIRECT_URI` | URI de redirection OAuth Spotify (optionnel — feature scan) |

---

## Structure

```
src/main/java/org/balancetonrappeur/
├── client/        # Spotify public API client (photo artistes)
├── spotify/       # Spotify OAuth client (scan bibliothèque)
│   ├── client/
│   └── dto/
├── config/        # Filtres (auth admin)
├── controller/    # Controllers MVC
├── dto/           # DTOs
├── entity/        # Entités JPA
├── exception/     # Exceptions custom
├── repository/    # Spring Data repositories
├── service/       # Logique métier
└── util/          # Utilitaires (normalisation chaînes...)

src/main/resources/
├── templates/     # Templates Thymeleaf
├── static/        # favicon, robots.txt, sitemap.xml
└── db/            # Scripts SQL (jdd.sql)
```

---

## Mentions légales

Ce projet est à vocation informative et citoyenne. Toutes les informations publiées sont issues de sources publiques vérifiables. Formulaire de [demande de retrait](https://balancetonrappeur.fr/legal#retrait) disponible sur le site.
