package edu.smartgate.reza.smartgateta;

/**
 * Created by Reza on 16-Dec-17.
 */

class ServerConfig {
    //labkom
    //private static final String SERVER_URL = "http://192.168.1.123/smartgate/";

    //rumah
    //protected static final String SERVER_URL = "http://192.168.100.10/smartgate/";

    //ruang dosen siskom
    //protected static final String SERVER_URL = "http://192.168.0.115/smartgate/";

    //hosting online
    protected static final String SERVER_URL = "http://smartgateta.xyz/smartgate/";

    //php
    static final String URL_LOGIN = SERVER_URL+"LoginUser.php";
    static final String URL_REGISTER = SERVER_URL+"RegisterUserLengkap.php";
    static final String URL_RIWAYAT = SERVER_URL+"AmbilStatusGerbang.php";
    static final String URL_UPDATE = SERVER_URL+"UpdateGbg.php";
    static final String URL_CHECK = SERVER_URL+"CheckStatusGerbang.php";

}
