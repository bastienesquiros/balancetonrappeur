# Protection juridique — Balance Ton Rappeur

## 1. Conseils pérennes

1. **Ne jamais formuler un jugement personnel** — toujours employer le conditionnel ou les formules "accusé de", "mis en examen pour", "condamné à". Ne jamais écrire "est coupable de" ou équivalent.

2. **Présomption d'innocence** — toute affaire sans condamnation définitive doit être clairement présentée comme telle. Les affaires "En cours" ou "Polémique" ne constituent pas une condamnation.

3. **Délai de réponse 72h (droit de réponse loi 1881)** — le tenir dans les faits. Si non réaliste, modifier en production à 7 jours ouvrés. Si vous recevez une mise en demeure, une réponse rapide et documentée réduit fortement le risque de passage en justice.

4. **Pas de données sensibles sans nécessité** — vous ne stockez pas d'orientations sexuelles, données médicales, etc. Continuez ainsi.

5. **Droit à l'oubli** — si une personne condamnée a purgé sa peine et que l'affaire est ancienne, une demande de retrait bien motivée est difficile à refuser juridiquement (droit à l'oubli numérique). Traitez ces cas avec bienveillance.

6. **Ne jamais nommer une victime** — notamment dans les cas d'agression sexuelle. Risque pénal direct (violation de l'anonymat des victimes, art. 2-2 CPP).

7. **Affaires classées sans suite** — s'assurer qu'elles sont bien marquées "Hors de cause" et que le wording le reflète clairement côté front. Ne pas écrire "innocenté" (juridiquement inexact), préférer "affaire classée sans suite".

8. **Emails soumetteurs** — mentionner sur les formulaires que l'email est utilisé uniquement pour le suivi de la demande et supprimé après traitement (obligation RGPD).

---

## 2. Formulations à utiliser / éviter

| ✅ À utiliser | ❌ À éviter |
|---|---|
| "Mis en cause pour..." | "Coupable de..." |
| "Accusé de..." | "A commis..." |
| "Fait l'objet d'une plainte pour..." | "Est un violeur / agresseur" |
| "Condamné par le tribunal de... à..." | "Criminel" (sans condamnation) |
| "Affaire classée sans suite" | "Innocenté" (juridiquement inexact) |
| "Procédure en cours" | "Jugé coupable" (avant verdict) |
| "Polémique suite à des propos tenus..." | "A tenu des propos racistes" (jugement) |

---

## 3. En cas de mise en demeure

1. **Ne pas supprimer immédiatement sans analyse** — une suppression précipitée peut être interprétée comme un aveu.
2. **Contacter un avocat spécialisé presse/internet** avant de répondre.
3. **Documenter la source** de l'information concernée (screenshots, URLs, dates).
4. **Utiliser le formulaire de retrait** pour tracer la demande (déjà en place via `withdrawal_requests`).
5. **Délais légaux** : 72h pour le droit de réponse (loi 1881), 30 jours pour une demande RGPD.

---

## 4. Sécurité technique (points de vigilance)

- **HTTPS obligatoire** en production (via Cloudflare ou certificat Let's Encrypt).
- **Rate limiting** sur les formulaires de soumission (anti-spam / abus) — à mettre en place si trafic significatif.
- **Pas d'injection SQL** — Spring Data JPA protège. Vérifier les requêtes natives si ajoutées.
- **Logs de modération** — trace de qui a publié quoi et quand déjà assurée via `created_at` et `submissions`.

---

## 5. RGPD — registre de traitement

Tenir un registre interne (fichier suffisant) documentant :
- Données collectées : emails soumetteurs, adresse IP (logs serveur).
- Finalité : suivi des demandes de soumission et de retrait.
- Durée de conservation : durée du traitement de la demande + délai raisonnable (ex. 6 mois).
- Pas de transmission à des tiers.

Aucune déclaration en ligne CNIL requise, mais le registre doit exister.

---

## 6. Ressources utiles

- [Loi du 29 juillet 1881 — liberté de la presse](https://www.legifrance.gouv.fr/loda/id/JORFTEXT000000877119)
- [LCEN — Loi pour la confiance dans l'économie numérique](https://www.legifrance.gouv.fr/loda/id/JORFTEXT000000801164)
- [RGPD — Règlement général sur la protection des données](https://www.cnil.fr/fr/rgpd-de-quoi-parle-t-on)
- [CNIL — Mentions légales](https://www.cnil.fr/fr/les-mentions-legales-sur-un-site-web)
