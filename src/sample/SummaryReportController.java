package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class SummaryReportController {
    public ObservableList<AccountSummaryReport> accounts = FXCollections.observableArrayList();
    FilteredList<AccountSummaryReport> filteredObjects;
    SortedList<AccountSummaryReport> sortedObjects;
    @FXML
    private TextField filter;

    @FXML
    private Button reset;

    @FXML
    private TableColumn<AccountSummaryReport, String> summaryOpeningDate;
    @FXML
    private TableColumn<AccountSummaryReport, Number> numAccounts;

    @FXML
    private TableView<AccountSummaryReport> summaryTable;


    public SummaryReportController(ObservableList<AccountSummaryReport> accounts) {
        this.accounts = accounts;
    }

    public void initialize() {
        summaryOpeningDate.setCellValueFactory(new PropertyValueFactory("openingDate"));
        summaryOpeningDate.setStyle("-fx-alignment: CENTER;");
        numAccounts.setCellValueFactory(new PropertyValueFactory("count"));
        numAccounts.setStyle("-fx-alignment: CENTER;");

        filteredObjects = new FilteredList<>(accounts, p -> true);
        sortedObjects  = new SortedList<>(filteredObjects);
        sortedObjects.comparatorProperty().bind(summaryTable.comparatorProperty());
        summaryTable.setItems(sortedObjects);

        FilteredList<AccountSummaryReport> filteredList = new FilteredList<>(accounts, b->true);
        filter.textProperty().addListener(((observableValue, old, newVal) -> {
            filteredList.setPredicate(account->{
                if(newVal == null || newVal.isEmpty())
                    return true;
                String lower = newVal.toLowerCase();
                return account.getOpeningDate().toLowerCase().contains(lower);
//                else if(String.valueOf(account.getCount()).indexOf(lower) != -1)
//                    return true;
            });
        }));
        sortedObjects = new SortedList<>(filteredList);
        sortedObjects.comparatorProperty().bind(summaryTable.comparatorProperty());
        summaryTable.setItems(sortedObjects);

        reset.setOnAction((actionEvent -> filter.setText("")));

    }
}
