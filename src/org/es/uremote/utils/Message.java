package org.es.uremote.utils;

public class Message {

	// Code de message
	public static final String CODE_AI		= "code_ai";
	public static final String CODE_KEYBOARD = "code_keyboard";
	public static final String CODE_COMBO	= "code_combo";
	public static final String CODE_MEDIA	= "code_media";
	public static final String CODE_VOLUME	= "code_volume";
	public static final String CODE_APP		= "code_app";
	public static final String CODE_CLASSIC	= "code_classic";
	
	// Gestion général
	public static final String HELLO_SERVER	= "hello_serveur";
	public static final String HELLO_CLIENT	= "hello_client";
	public static final String TEST_COMMAND	= "test_command";
	public static final String KILL_SERVER	= "kill_server";
	public static final String SHUTDOWN		= "shutdown";
	public static final String MONITOR_SWITCH_WINDOW = "switch_window";

	// Gestion des touches du clavier 
	public static final String KB_ENTER		= "enter";
	public static final String KB_SPACE		= "space";
	public static final String KB_BACKSPACE = "backspace";
	public static final String KB_ESCAPE	= "escape";
	public static final String KB_ALT_F4	= "alt_f4";
	public static final String KB_CTRL_ENTER= "ctrl_enter";
	
	// Gestion du multi-media
	public static final String MEDIA_PLAY_PAUSE	= "play_pause";
	public static final String MEDIA_STOP		= "stop";
	public static final String MEDIA_PREVIOUS	= "previous";
	public static final String MEDIA_NEXT		= "next";
	
	// Gestion du son
	public static final String VOLUME_MUTE	= "volume_mute";
	public static final String VOLUME_UP	= "volume_up";
	public static final String VOLUME_DOWN	= "volume_down";
	
	// Gestion des fenêtres et applications
	public static final String APP_GOM_PLAYER	= "app_gom_player";
	public static final String KILL_GOM_PLAYER	= "kill_gom_player";
	public static final String GOM_PLAYER_STRETCH	= "gom_player_stretch";
	
	// Explorateur de fichiers
	public static final String OPEN_DIR		= "open_directory";
	public static final String OPEN_FILE	= "open_file";
	
	// Reply
	public static final String REPLY_VOLUME_ON		= "Mute : Volume is on";
	public static final String REPLY_VOLUME_MUTED	= "Mute : Volume is off";
	
	// Intelligence Artificielle
	public static final String AI_MUTE	= "ai_mute";
	
	public static final String ERROR		= "error";

}
