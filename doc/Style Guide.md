Style Guide
===

[Google Java Style Guide]

###Formatting
- Geen regeleinde voor de accolade
- Regeleinde na de openings accolade
- Regeleinde voor de sluitende accolade
- Regeleinde na de accolade tenzij het onderdeel is van een multi-block statement (``if/else``, ``try/catch/finally``)

```
if (statement == true) {
  try {
    // Doe iets
  } catch (error e) {
    // Doe nog iets
  }
} else {
  // Doe nog iets anders
};
```

- Limiet van 100 tekens per zin
- Spaties voor en na de tekens ``+, -, *, /, <, >, =, ==, !=`` etc. (``(x + 1) / 2``)

###Naamgeving
- Class namen worden geschreven in ``UpperCamelCase``
- Functie namen worden geschreven in ``lowerCamelCase``
- Variabelen worden geschreven in ``lowercase_separated_by_underscores``
- Constanten worden geschreven in ``CONSTANT_CASE``





[Google Java Style Guide]:https://google-styleguide.googlecode.com/svn/trunk/javaguide.html