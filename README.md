Robotica
========

A project for "Robotica en Mechatronica" (dutch) where we control a Roomba robot, which we called Kate or K8 in short, to navigate through an unkwown room and map the environment.

Brains
------

This package will be used for the real logic of Kate. The algorithms that will make sure Kate knows where she is, what her available options are etcetera.

The Brains package only communicates with the Emulator.

Roomba
------

The Roomba package will be used to hold all the commands to talk to Kate. This package will have the necessary knowledge to communicate with Kate, including the handshake protocol.

The Roomba package only communicates with the Emulator.

Emulator
--------

This package is the bridge between the Brains and the Roomba. This package will receive commands from the Brains, parse them, show them on the screen, and then pass the commands to the Roomba package. The Roomba package will give feedback to the Emulator, which can then pass this information to the Brains if necessary.