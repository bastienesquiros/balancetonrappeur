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
- **Hébergement** — Docker + Traefik (TLS auto) + Cloudflare

---

## Fonctionnalités

- Fiches rappeurs avec timeline des affaires
- Catégories d'affaires
- Statuts juridiques 
- Statuts rappeurs 
- Recherche avec autocomplétion (navbar)
- Filtres par catégorie et statut juridique sur les pages Rappeurs et Affaires
- Timeline globale des affaires
- Page statistiques
- Soumission communautaire (modération obligatoire avant publication)
- Demande de retrait / correction (`/legal#retrait`)
- **Scan Spotify** — analyse les liked songs et playlists, détecte les rappeurs concernés et permet de les retirer directement
- Dashboard admin (`/admin`) — gestion des soumissions, retraits, sync Spotify, force sync photo
- Digest mail quotidien (8h) avec les soumissions et retraits en attente
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

Pour le mail en dev, utiliser [Mailpit](https://github.com/axllent/mailpit) (SMTP sur le port 1025, interface sur http://localhost:8025).

---

## Déployer avec Docker

```bash
# 1. Créer le fichier d'environnement
cp .env.example .env
nano .env

# 2. Build et démarrage (Traefik + app + PostgreSQL)
docker compose up -d --build
```

L'app tourne derrière Traefik qui gère le TLS automatiquement via Let's Encrypt.

---

## Variables d'environnement

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL (ex: `jdbc:postgresql://db:5432/balancetonrappeur`) |
| `DB_USERNAME` | Utilisateur BDD |
| `DB_PASSWORD` | Mot de passe BDD |
| `MAIL_HOST` | Hôte SMTP |
| `MAIL_PORT` | Port SMTP (587 pour Resend) |
| `MAIL_USERNAME` | Utilisateur SMTP |
| `MAIL_PASSWORD` | Mot de passe SMTP |
| `BTR_MAIL_FROM` | Adresse expéditeur du digest admin |
| `BTR_MAIL_NOREPLY` | Adresse expéditeur des mails transactionnels — fallback sur `BTR_MAIL_FROM` si absent |
| `BTR_MAIL_DIGEST_TO` | Destinataire du digest quotidien |
| `BTR_ADMIN_PASSWORD` | Mot de passe dashboard `/admin` |
| `SPOTIFY_CLIENT_ID` | Client ID Spotify (optionnel — feature scan) |
| `SPOTIFY_CLIENT_SECRET` | Client Secret Spotify (optionnel — feature scan) |
| `SPOTIFY_REDIRECT_URI` | URI de redirection OAuth Spotify (ex: `https://balancetonrappeur.fr/scan/callback`) |

---

## Structure

```
src/main/java/org/balancetonrappeur/
├── client/        # Spotify public API (photo artistes via Client Credentials)
├── spotify/       # Spotify OAuth (scan bibliothèque utilisateur)
│   ├── client/
│   └── dto/
├── config/        # Filtres (auth admin)
├── controller/    # Controllers MVC
├── dto/           # DTOs (formulaires, résultats)
├── entity/        # Entités JPA + enums
├── exception/     # Exceptions custom
├── repository/    # Spring Data repositories
├── service/       # Logique métier
└── util/          # Utilitaires

src/main/resources/
├── templates/     # Templates Thymeleaf
├── static/        # favicon, robots.txt, sitemap.xml
└── db/            # Scripts SQL (jdd.sql)
```

---

## Mentions légales

Ce projet est à vocation informative et citoyenne. Toutes les informations publiées sont issues de sources publiques vérifiables. Formulaire de [demande de retrait](https://balancetonrappeur.fr/legal#retrait) disponible sur le site.
