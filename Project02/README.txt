To run the StartClientAndServer class in Eclipse, mate-terminal needs to be installed an available.

All the files can be run manually by fist generating all necessary files by running StartClientAndServer.java form
Eclipse and then run the client and server from bin/.

Example:

    //first terminal
    cd bin
    java main.server 1234

    // antother terminal
    cd bin
    java main.client localhost 1234 Victoria Zoran password

Available usernames are:

    Victoria Zoran  -- doctor
    Svetlana Mercer -- doctor
    Peter Miller -- nurse
    Adam Persson -- nurse
    Donny Nilsso -- user
    Mr Black -- agency

    All users have the password "password".

All the available journals are located in the folder journals and have a doctor, nurse and division assigned to them.

Client commands:

    username -- show usename
    access -- show access level
    read -- read journal
    edit -- edit journal
    remove -- remove journal
    divison -- show assigned division
    new -- create a new journal
    quit -- exits program
