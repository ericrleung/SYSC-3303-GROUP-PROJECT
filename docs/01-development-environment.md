# 01 - Development Environment

Instructions below are for Ubuntu 20.04. They may differ for other operating systems.

### Install Java 8
```sh
$ sudo apt-get openjdk-8-jdk -y
$ # Add JAVA_HOME to environment variables
$ echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >> ~/.profile
```
### Install eclipse
Note: There are different ways to install eclipse. The instructions below show just one possible method.

This will install eclipse to ~/applications/eclipse and will add eclipse to the path. Running `eclipse` anywhere from a bash should launch the IDE.

```sh
$ mkdir ~/tmp && cd ~/tmp
$ wget -O eclipse.tar.gz https://eclipse.mirror.rafal.ca/technology/epp/downloads/release/2020-12/R/eclipse-java-2020-12-R-linux-gtk-x86_64.tar.gz
$ tar xfz eclipse.tar.gz
$ mkdir -p ~/applications
$ mv eclipse ~/applications/eclipse
$ echo 'PATH="$HOME/applications/eclipse:$PATH" >> ~/.profile
```

### Setup project
This assumes you have cloned the project under ~/dev/sysc-3303-group-project
```sh
$ cd ~/dev/sysc-3303-group-project
$ cd project
# make sure you can run the tests.
$ ./gradlew test
# Generate eclipse project (optional)
$ ./gradlew eclipse
```

Now you can import the folder ~/dev/sysc-3303-group-project/project into eclipse.

# 02 - Generating Class Diagrams

Gradle and Plantuml have been configured for our project. Gradle will generate plantuml files from the source code which can then be edited before being produced into a png.

### Generate class diagrams
This assumes you have cloned the project under ~/dev/sysc-3303-group-project

```sh
$ cd ~/dev/sysc-3303-group-project
$ cd project
# make sure you can build the code
$ ./gradlew build
# Generate plantuml files into project/diagrams
$ ./gradlew buildClassDiagram
# Generate png files
$ java -jar ../tools/plantuml.jar diagrams/class_diagram.plantuml
# This will give you the file project/diagrams/class_diagram.png
# Open this file to take a look.
```