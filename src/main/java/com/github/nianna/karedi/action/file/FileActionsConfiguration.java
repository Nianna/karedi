package main.java.com.github.nianna.karedi.action.file;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.SongSaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.EXPORT_AS_DUET;
import static main.java.com.github.nianna.karedi.action.KarediActions.EXPORT_AS_SINGLEPLAYER;

@Configuration
class FileActionsConfiguration {

    @Bean
    NewKarediAction exportAsSinglePlayerAction(AppContext appContext, SongContext songContext, SongSaver songSaver) {
        return new ExportTracksAction(EXPORT_AS_SINGLEPLAYER, 1, appContext, songContext, songSaver);
    }

    @Bean
    NewKarediAction exportAsDuetAction(AppContext appContext, SongContext songContext, SongSaver songSaver) {
        return new ExportTracksAction(EXPORT_AS_DUET, 2, appContext, songContext, songSaver);
    }
}
