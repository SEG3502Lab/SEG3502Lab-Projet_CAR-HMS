# Cairn (CAR-HMS)

Projet de session du cours SEG 3502 (automne 2026), équipe 2.

Cairn est un système de gestion d'événements de randonnée. Les randonneurs s'inscrivent aux sorties, les organisateurs certifiés les créent et les administrateurs gèrent les adhésions. Pour l'instant, le dépôt contient le domaine et les contrats Cucumber. La persistance et l'interface arrivent au livrable 3.

## Pour lancer le projet

Il faut Java 21. Gradle est fourni avec le wrapper.

```bash
./gradlew test
```

Cette commande compile le projet et exécute les 68 scénarios des contrats. Le rapport se trouve ensuite dans `build/reports/tests/test/index.html`.

L'application ne démarre pas encore avec `bootRun` : les fabriques, les dépôts et les services externes n'ont que des stubs dans les sources de test.

## Organisation du code

```text
src/main/kotlin/CAR_HMS
├── application
│   ├── dtos                  requêtes et résultats
│   ├── services              courriel, météo, utilisateur connecté, etc.
│   └── usecases              commandes et leur implémentation
└── domain
    ├── common                noyau partagé
    ├── hikes                 randonnées et inscriptions
    ├── members               randonneurs et organisateurs
    ├── trails                sentiers de la CCN
    ├── authentication        comptes et autorisations
    ├── notifications         contenu des courriels
    └── reports               utilisation des sentiers

src/test
├── kotlin/CAR_HMS/contracts  définitions d'étapes et stubs
└── resources/contracts       fichiers .feature
```

Chaque sous-domaine expose ses opérations à la couche application par une façade.

## Intégration continue

Le workflow **Contrats Cucumber** exécute les contrats sur GitHub Actions. Il se lance à la main depuis l'onglet Actions.

## Documentation

Le rapport du livrable 2 est dans [`docs/Livrable2_CAR-HMS.md`](docs/Livrable2_CAR-HMS.md). On y trouve le modèle du domaine, les diagrammes de séquence et nos décisions de conception.

## Équipe

| Nom | GitHub |
| --- | --- |
| Mehdi Semmar | [MehdiSmr](https://github.com/MehdiSmr) |
| Salma Sajid | [SjSalma](https://github.com/SjSalma) |
