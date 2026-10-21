# User Stories – seeCph

---

## Bruger (besøgende)

### US1 - Opret bruger
As a – Som en besøgende der ønsker at oprette sig som bruger.
I want – Vil jeg kunne registrere mig med navn, email, telefon og password.
So that – Så jeg kan gemme favoritevents og like events.

Acceptkriterier.
Given – Givet at jeg ikke er oprettet som bruger.
When – Når jeg udfylder registreringsformularen og trykker "Opret bruger".
Then – Da skal jeg oprettes med rollen "bruger", og hvis emailen allerede er i brug, skal jeg få en fejlbesked.

Estimation: `Small`

---

### US2 - Log ind
As a – Som en oprettet bruger, arrangør eller admin.
I want – Vil jeg kunne logge ind med min email og mit password.
So that – Så jeg får adgang til de funktioner, min rolle giver mig.

Acceptkriterier.
Given – Givet at jeg har en konto i systemet.
When – Når jeg indtaster email og password og trykker "Log ind".
Then – Da skal jeg modtage et token, der giver adgang ud fra min rolle, og ved forkert email eller password skal jeg få en fejlbesked.

Estimation: `Medium`

---

### US3 - Søg og filtrer events
As a – Som en bruger der vil finde noget at lave i København.
I want – Vil jeg kunne søge og filtrere events efter tekst, dato, kategori og postnummer.
So that – Så jeg slipper for at besøge flere forskellige hjemmesider for at finde events.

Acceptkriterier.
Given – Jeg er på forsiden af event-siden.
When – Jeg skriver et søgeord og/eller vælger en dato, kategori eller et postnummer.
Then – Skal jeg se en liste af events, der matcher mine filtre, sorteret efter dato og delt op i sider.


Estimation: `Medium`

---

### US4 - Se eventdetaljer
As a – Som en bruger der har fundet et interessant event.
I want – Vil jeg kunne klikke ind og se alle detaljer om eventet.
So that – Så jeg kan beslutte om jeg vil deltage, og finde info om tid, sted og billetter.

Acceptkriterier.
Given – Jeg ser en liste af events.
When – Jeg klikker på et specifikt event.
Then – Skal jeg se titel, beskrivelse, billede, dato/tid, adresse, pris og link til billetkøb.

Estimation: `Small`

---

### US5 - Gem events som favoritter
As a – Som en bruger der er logget ind.
I want – Vil jeg kunne gemme events som favoritter og fjerne dem igen.
So that – Så jeg nemt kan finde de events, jeg overvejer at deltage i.

Acceptkriterier.
Given – Jeg er logget ind som bruger og ser et event.
When – Jeg klikker "Gem som favorit" eller "Fjern fra favoritter".
Then – Skal eventet tilføjes til eller fjernes fra min favoritliste, og det samme event må kun stå på listen én gang.

Estimation: `Small`

---

### US6 - Like events
As a – Som en bruger der er logget ind.
I want – Vil jeg kunne like events, jeg synes godt om.
So that – Så jeg kan vise hvad jeg kan lide, og finde mine likede events igen.

Acceptkriterier.
Given – Jeg er logget ind som bruger og ser et event.
When – Jeg klikker "Like" eller fjerner mit like.
Then – Skal eventet tilføjes til eller fjernes fra min liste over likede events, uafhængigt af mine favoritter.

Estimation: `Small`

---

### US7 - Administrer egen profil
As a – logget ind som bruger, arrangør eller admin.
I want – Vil jeg kunne se og rette mine oplysninger og skifte mit password.
So that – Så mine oplysninger er korrekte, og jeg kan holde min konto sikker.

Acceptkriterier.
Given – Jeg er logget ind.
When – Jeg retter navn, email eller telefon, eller skifter password med mit nuværende password.
Then – Skal ændringerne gemmes, og hvis det nuværende password er forkert, eller de nye passwords ikke matcher, skal jeg få en fejlbesked.

Estimation: `Small`

---

### US8 - Slet egen konto
As a – Som en bruger eller arrangør.
I want – Vil jeg kunne slette min konto.
So that – Så mine oplysninger ikke længere ligger på platformen, hvis jeg ikke vil bruge den mere.

Acceptkriterier.
Given – Jeg er logget ind som bruger eller arrangør.
When – Jeg klikker "Slet min konto" og bekræfter.
Then – Skal min konto slettes, og mine favoritter og likes fjernes sammen med den.

Estimation: `Small`

---

## Arrangør

### US9 - Opret arrangørkonto
As a – Som en arrangør der vil promovere mine events.
I want – Vil jeg kunne oprette en arrangørkonto adskilt fra almindelige brugere.
So that – Så jeg får adgang til at oprette events og annoncere.

Acceptkriterier.
Given – Jeg er ikke oprettet som arrangør.
When – Jeg udfylder firmanavn, navn, email, telefon og password.
Then – Skal jeg oprettes med rollen "arrangør" og status "afventer", og først kunne logge ind når en admin har godkendt mig.

Estimation: `Medium`

---

### US10 - Opret event
As a – Som en logget ind arrangør.
I want – Vil jeg kunne oprette et nyt event med alle relevante informationer.
So that – Så mit event bliver synligt for brugere på platformen.

Acceptkriterier.
Given – Jeg er logget ind som arrangør.
When – Jeg udfylder titel, beskrivelse, dato/tid, adresse, kategori, pris, link og billede og trykker "Opret event".
Then – Skal eventet gemmes, knyttes til mig og vises i søgeresultaterne, og hvis datoen ligger i fortiden, skal jeg få en fejlbesked.

Estimation: `Medium`

---

### US11 - Se, rediger og slet egne events
As a – Som en arrangør.
I want – Vil jeg kunne se mine egne events og redigere eller slette dem.
So that – Så jeg kan opdatere info eller fjerne eventet, hvis det bliver aflyst.

Acceptkriterier.
Given – Jeg er logget ind som arrangør og ser mine egne events.
When – Jeg klikker "Rediger" eller "Slet" på et af mine events.
Then – Skal ændringerne gemmes, eller eventet fjernes fra platformen, men kun hvis eventet tilhører mig.

Estimation: `Small`

---

### US12 - Annoncér eget event
As a – Som en arrangør der vil have mere synlighed.
I want – Vil jeg kunne oprette en annonce for mit event på forsiden i en valgt periode.
So that – Så flere brugere ser og deltager i mit event.

Acceptkriterier.
Given – Jeg er logget ind som arrangør og har oprettet et event.
When – Jeg vælger placering (forsidebanner eller fremhævning) og en start- og slutdato.
Then – Skal annoncen oprettes og vises på forsiden i den valgte periode, og jeg skal kun kunne annoncere mine egne events.

Estimation: `Medium`

---

## Admin

### US13 - Godkend arrangører
As a – Som en admin.
I want – Vil jeg kunne se arrangører, der venter på godkendelse, og godkende eller afvise dem.
So that – Så kun seriøse arrangører kan oprette events på platformen.

Acceptkriterier.
Given – Jeg er logget ind som admin.
When – Jeg åbner listen over arrangører med status "afventer" og ændrer status på en af dem.
Then – Skal arrangørens status opdateres, og en godkendt arrangør skal kunne logge ind.

Estimation: `Small`

---

### US14 - Administrer events, annoncer og brugere
As a – Som en admin.
I want – Vil jeg kunne oprette, rette og fjerne alle events, annoncer og brugere.
So that – Så platformen holdes fri for upassende, forældet eller forkert indhold.

Acceptkriterier.
Given – Jeg er logget ind som admin.
When – Jeg vælger et event, en annonce eller en bruger og retter eller sletter det.
Then – Skal ændringen gemmes med det samme, også for events oprettet af andre eller indsamlet automatisk.

Estimation: `Medium`

---

## System (dataindsamling)

### US15 - Automatisk indsamling af events
As a – Som platformen (systemet).
I want – Vil jeg automatisk og dagligt hente events i København.
So that – Så brugerne får et samlet overblik over events, uden at arrangørerne selv skal oprette dem.

Acceptkriterier.
Given – Systemet har adgang til API(er).
When – Det planlagte job kører hver nat.
Then – Skal nye events gemmes, eksisterende events opdateres uden dubletter, slettes når de udgår.

Estimation: `Large`

---