# Vienkārša CO-OP spēle

## Ievads

Šis projekts ir vienkārša 2 spēlētāju CO-OP spēle. Šis projekts tika veidots kā daļa no LUDF OOP kursa.

### Motivācija

Mēs izvēlējāmies taisīt spēli kā mūsu projektu, jo mēs vēlējāmies uztaisīt ko interesantu.
Neviens no mums nekad nav strādājis pie nopietnākām spēlēm, kā pie komandrindas labirintiem un tamlīdzīgiem prastiem projektiem.
Ar šo projektu mēs vēlamies iepazīt vienkāršu spēļu dizainu, un saprast grūtības, kas slēpjas šajā nozarē.

___

## Projekta struktūra un resursi

Projekts ir realizēts izmantojot java 20 un kompilēts izmantojot maven.

Izmantotās resursu pakas spēles realizācijā:

* https://brullov.itch.io/oak-woods
* https://sventhole.itch.io/bandits

___

## Uzstādīšana

### Spēles uzstādīšana

#### Bez kompilēšanas

Ja vēlas spēli palaist bez nepieciešamības kompilēt, tad ir iespējams nolādēt nokompilēto versiju no `Releases` sadaļas.
Papildus uz datora ir nepieciešama Java 20

#### Ar kompilēšanu

Ja vēlaties kompilēt spēli, ir nepieciešama openjdk 20.
Pēc tam spēli var atvērt caur Eclipse, vai IntelliJ un eksportēt.

### Savienošanās, lai spēlētu kopā

Viens no spēlētājiem ir serveris, kamēr otrs ir klients.

Spēlētājam, kurš ir serveris ir jāuzsāk spēle ar `Host` pogu, kur būs jāievada ports pie kura slēgsies otrs spēlētājs.
Ir svarīgi atcerēties, ka otrajam spēlētājam ir jābūt piekļuvei pie servera spēlētāja porta, kas nozīmē, ka ja abi spēlētāji nav LAN, tad servera spēlētājam ir jāatklāj ports, lai otrs spēlētājs varētu pieslēgties.
Servera spēlētājam arī būs jāizvēlās parole, ko otrs spēlētājs izmantos, lai pieslēgtos pie pirmā spēlētāja.

Klienta spēlētājam, tad kad servera spēlētājs ir uzsācis spēli ir jāpievienojas izmantojot `Join` pogu.
Klienta spēlētājam būs jāievada servera spēlētāja adrese, ports un parole, lai varētu pieslēgties spēlei.
