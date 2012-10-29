Robotica
========

A project for "Robotica en Mechatronica" (dutch)

Brains
------

This package will be used for the real logic of the robot. The algorithms that will make sure the robot knows where is, what the available options are etcetera.

The Brains package only communicates with the Emulator.

Roomba
------

The Roomba package will be used to hold all the commands to talk to the robot. Here alle commands that are given will be parsed and passed to the robot.

The Roomba package only communicates with the Emulator.

Emulator
--------

This package is the bridge between the Brains and the Roomba. This package will receive commands from the Brains, parse them, show them on the screen, and then pass the commands to the Roomba package. The Roomba package will give feedback to the Emulator, which can then pass this information to the Brains if necessary.