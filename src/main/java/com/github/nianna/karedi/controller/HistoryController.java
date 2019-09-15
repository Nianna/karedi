package main.java.com.github.nianna.karedi.controller;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.History;
import main.java.com.github.nianna.karedi.util.BindingsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoryController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private ListView<Command> list;
	@FXML
	private MenuItem clearMenuItem;

	private AppContext appContext;
	private boolean changedByUser = false;

	@Autowired
	private History history;

	@FXML
	public void initialize() {
		list.setCellFactory(getCellFactory());
	}

	@FXML
	private void handleClear() {
		history.clear();
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		list.setDisable(false);
		list.setItems(history.getList());
		clearMenuItem.disableProperty().bind(BindingsUtils.isEmpty(list.getItems()));
		list.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
		history.activeCommandProperty().addListener(this::onActiveCommandChanged);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private void onSelectedItemChanged(Observable obs, Command oldCmd, Command newCmd) {
		int oldIndex = history.getActiveIndex();
		int newIndex = list.getItems().indexOf(newCmd);
		if (newIndex != oldIndex) {
			changedByUser = true;
			int difference = newIndex - oldIndex;
			KarediActions historyCmd = difference > 0 ? KarediActions.REDO : KarediActions.UNDO;
			for (int i = 0; i < Math.abs(difference); ++i) {
				appContext.execute(historyCmd);
			}
			changedByUser = false;
		}
	}

	private void onActiveCommandChanged(Observable obs, Command oldCmd, Command newCmd) {
		list.getSelectionModel().select(newCmd);
		if (!changedByUser) {
			list.scrollTo(newCmd);
		}
	}

	private Callback<ListView<Command>, ListCell<Command>> getCellFactory() {
		return (lv -> new ListCell<Command>() {
			@Override
			protected void updateItem(Command item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.getTitle() == null) {
					setText(null);
				} else {
					// use getTitle() instead of default toString()
					setText(item.getTitle());
				}
			}
		});
	}
}
