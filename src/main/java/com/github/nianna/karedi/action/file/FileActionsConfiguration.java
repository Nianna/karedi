package main.java.com.github.nianna.karedi.action.file;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongSaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.EXPORT_AS_DUET;
import static main.java.com.github.nianna.karedi.action.KarediActions.EXPORT_AS_SINGLEPLAYER;

@Configuration
class FileActionsConfiguration {

    @Bean
    NewKarediAction exportAsSinglePlayerAction(AppContext appContext, DisplayContext displayContext, SongSaver songSaver) {
        return new ExportTracksAction(EXPORT_AS_SINGLEPLAYER, 1, appContext, displayContext, songSaver);
    }

    @Bean
    NewKarediAction exportAsDuetAction(AppContext appContext, DisplayContext displayContext, SongSaver songSaver) {
        return new ExportTracksAction(EXPORT_AS_DUET, 2, appContext, displayContext, songSaver);
    }
}
