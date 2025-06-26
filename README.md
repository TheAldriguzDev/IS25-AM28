<h1 align="center">
    <img src="/src/main/resources/imgs/galaxy_logo.png" alt="logo" width="350"/>
    <br>
    Galaxy Trucker
    <br>
</h1>
<h4 align="center">Software Engineering Project for Politecnico di Milano 2024 - 2025</h4>
<p align="center">
    Matteo Aldrigo •
    Gabriele Bertirotti •
    Filippo Baldissara Gasparinetti
</p>

## Project Overview
Digital implementation of the game [Galaxy Trucker](https://www.craniocreations.it/prodotto/galaxy-trucker) by [Cranio Creations](https://www.craniocreations.it/).

Each game can be played from **2 to 4** players (clients). 

The players can play from a Textual User Interface, best known as TUI, or from a Graphical User Interface, known also as GUI.

## 📋 Implemented Features
In the project we implemented all the requirements plus we decide to develop three advanced features.

| Feature                      | Implemented |
|:-----------------------------|:-----------:|
| Complete rules               |      🟢       |
| TUI                          |      🟢      |
| GUI                          |      🟢      |
| Socket                       |      🟢      |
| RMI                          |      🟢      |
| Multiple games               |      🟢      |
| Persitency                   |      🔴      |
| Resilience to disconnections |      🟢      |
| Test flight                  |      🟢      |

Legend: 
- 🔴 Not implemented
- 🟡 Implementing 
- 🟢 Implemented

### Used Tools

Support tools used during the development of this project:

| Library/Plugin | Description                             |
|:---------------|:----------------------------------------|
| Maven          | Project Managing, Java build automation |
| JavaFX         | Graphic packages to support the GUI     |
| Junit          | Framework for code testing              |

### How to use
The software can be run on **Windows**, **Linux**, and **macOS**.

To use the software, it is mandatory to have [Java JDK 23.0.2 or higher](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html) installed on your machine.

1. **Java JDK must be installed.**
2. Download the client and server JAR files. You can find them [here](https://github.com/TheAldriguzDev/IS25-AM28/tree/all-merged/deliverables).
3. Start the server: open a terminal in the folder containing the server JAR and run  
   `java -jar IS25-AM28-server.jar`
4. Start the client: open a terminal in the folder containing the client JAR and run  
   We currently support MacOS (silicon) and Linux: 
   - linux: `java -jar IS25-AM28-client-linux.jar`
   - macos: `java -jar IS25-AM28-client-macos.jar`
5. Follow the instructions shown in each terminal to play.