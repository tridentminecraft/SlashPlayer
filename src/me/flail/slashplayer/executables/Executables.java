package me.flail.slashplayer.executables;

import me.flail.slashplayer.tools.Logger;

public class Executables extends Logger {


	public enum Exe {

		TELEPORT,
		SUMMON,
		HEAL,
		FEED,
		KICK,
		KILL,
		BURN,
		ENDERCHEST,
		OPENINVENTORY,
		CLEARINVENTORY,
		RESTOREINVENTORY,
		MUTE,
		UNMUTE,
		BAN,
		UNBAN,
		WHITELIST,
		FREEZE,
		UNFREEZE,
		FLY,
		TOGGLEFLY,
		FRIEND,
		REPORT,
		GAMEMODE,
		GAMEMODESURVIVAL,
		GAMEMODECREATIVE,
		GAMEMODESPECTATOR,
		GAMEMODEADVENTURE,
		BACKBUTTON,
		CONFIRMACTION,
		CANCELACTION;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static Exe get(String s) {
			return Exe.valueOf(s.toUpperCase().replaceAll("[^A-Z]", ""));
		}

	}

}
