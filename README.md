Rembrandt College App
===

Deze app geeft de nieuws updates weer van een over internet verkregen XML file.

Functies
---
* De app moet op het internet de XML file kunnen downloaden en kunnen scannen op onderwerpen en inhoud.
* De XML file moet wroden opgeslagen in de cache zodat de berichten ook tijdens offline gelezen kunnen worden.
* Als de versie van de gedownloade file hetzelfde is als de XML file op de server wordt de gecachde file gebruikt en de file op internet niet gedownload.
* De onderwerpen van de XML file worden weergegeven in een ListView waar men door kan scrollen.
* De eerste tien berichten worden geladen. Het laatste item in de ListView is een vakje waar als men hier op klikt weer tien niewe berichten worden geladen.
* Als in de ListView bovenaan naar beneneden wordt geswiped, wordt er op internet gekeken of er een nieuwe versie aanwezig is van de XML file. Als dat zo is wordt deze ingeladen.
* Als er op een item wordt geklikt opent er een nieuw scherm met de content. Dit gebeurt in een Fragment zodat er beter gebruikt wordt gemaakt van de grootte van het scherm van tablets. Links staat een lijst met onderwerpen en rechts op het scherm verschijnt de content.
* Bij de content hoort ook een afbeelding dat moet worden gedownload. De link van de afbeelding staat in de XML file. Deze wordt opgeslagen in de cache waardoor die niet steeds opnieuw moet worden gedownload.
* Er is een side menu aanwezig waarop verschillende items staan:
	* Nieuws: Als men hier op tikt verschijnt de newsfeed van de XML file. (Al het bovenstaande)
	* Website: Er wordt een WebView geopend waarin de website wordt getoond.
	* Facebook: Als de facebook app is geinstalleerd wordt de facebook app geopend op de pagina van het Rembrandt College. Indien dit niet het geval is wordt er in de app een WebView geopend waar er naar de site wordt gegaan.
	* Twitter: Zelfde als bij facebook, maar dan twitter.
	* It's Learning: Er wordt een WebView geopend waarin de website wordt getoond.
	* Magister: Als de Magister app is geinstalleerd wordt de app geopend. Indien dit niet het geval is wordt er in de app een WebView geopend waarin de site wordt weergegeven.
	* Opties (nog niet zeker):
		* Open links in de app/standaard webbrowser
		* Leeg cache
		* Meer?

Code
---

Het programma wordt geschreven in xml en java. Xml wordt gebruikt voor statische elementen en java wordt gebruikt voor de dynamische content.

De app wordt geschreven voor android met minimaal API level 9 (Android 2.3 Gingerbread). De Fragments en Actionbar API's werden pas later geintroduceerd, maar kunnen toch gebruikt worden door middel van de support packages.

De volgende libraries/APIs worden gebruikt:

Er wordt veel gebruik gemaakt van de package android.widget voor Buttons, ImageViews etc.
android.View voor het aanspreken van widgets, OnClickListeners plaatsen etc.
Voor debugging wordt gebruik gemaakt van android.util.Log
Voor het weergeven en knippen van afbeeldingen wordt gebruik gemaakt van android.graphics
android.content.Intent voor het overschakelen naar een nieuwe activity
android.support.v4.app.Fragment voor het gebruik van fragments


