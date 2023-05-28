# CO-OP spēle

## Ievads

Projekts tika veidots kursa OOP ietvaros. Projekta laikā tika izveidota 2 spēlētāju CO-OP 
spēle. Pašlaik implementētas ir klases, lai viegli pievienotu jaunus pretiniekus, cīņas
elementi un spēlētāju kustību un mijiedarbība ar apkārtējo vidi.

### Motivācija

Mēs izvēlējāmies taisīt spēli kā mūsu projektu, jo atšķirībā no citiem Javas pielietojumiem, šis
viennozīmigi rada interesi. Neviens no mums nekad nav strādājis pie spēles izstrādes. Mūsu 
repertuārā ir mājaslapu izveide, komandrindu labirinti un līdzīga mēroga projekti.
Ar šo projektu mēs vēlamies izaicināt sevi un iepazīt spēļu dizainu, lai atrastu pielietojumu
pašreizējamajām programmēšanas prasmēm.

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
Papildus tev būs nepieciešams ielādēt Java 20.

#### Ar kompilēšanu

Ja vēlaties kompilēt spēli, ir nepieciešams openjdk 20.
Pēc tam spēli var atvērt caur Eclipse vai IntelliJ, palaižot iekš rīka vai to eksportējot.

### Savienošanās, lai spēlētu kopā

Lai izveidotu CO-OP bāzi, jeb mūsu gadījumā divu spēlētāju spēli, vienam no spēlētājiem būs
jābūt serverim, bet otram klientam. Klients pieslēdzas pie servera un pārsūta savus datus, kuri tiek pārstrādāti
un attiecīgi attēloti iekš spēles loga.

Spēlētājam, kurš ir serveris ir jāuzsāk spēle ar `Host` pogu, kur būs jāievada ports pie kura slēgsies otrs spēlētājs.
Ir svarīgi atcerēties, ka otrajam spēlētājam ir jābūt piekļuvei pie servera spēlētāja porta, kas nozīmē, ka ja abi spēlētāji nav LAN, tad servera spēlētājam ir jāatklāj ports, lai otrs spēlētājs varētu pieslēgties.
Servera spēlētājam arī būs jāizvēlās parole, ko otrs spēlētājs izmantos, lai pieslēgtos pie pirmā spēlētāja.

Klienta spēlētājam, tad kad servera spēlētājs ir uzsācis spēli ir jāpievienojas izmantojot `Join` pogu.
Klienta spēlētājam būs jāievada servera spēlētāja adrese, ports un parole, lai varētu pieslēgties spēlei.
