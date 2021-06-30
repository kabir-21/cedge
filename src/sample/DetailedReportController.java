package sample;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;

public class DetailedReportController {
    public ObservableList<AccountDetailedReport> accounts = FXCollections.observableArrayList();
    HashMap<String,TableColumn<AccountDetailedReport,?>> columnsMap = new HashMap<>();
    FilteredList<AccountDetailedReport> filteredObjects;
    SortedList<AccountDetailedReport> sortedObjects;
    ObservableList<TableColumn<AccountDetailedReport,?>> colList;
    ObservableList<String> removableList;

    DetailedReportController(ObservableList<AccountDetailedReport> accounts){
        this.accounts = accounts;
    }
    @FXML
    private ListView<String> canBeAdded;
    @FXML
    private ComboBox<String> columnRemover;
    @FXML
    private TableView<AccountDetailedReport> table;
    @FXML
    private TextField aType,bID,aID,oD,mName;
    @FXML
    private TableColumn<AccountDetailedReport, String> name,opDate,accType,accID,branch;
    @FXML
    private Button reset,removeColumnButton,addColumnButton;

    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<>("merchantName"));
        name.setStyle("-fx-alignment: CENTER;");
        accID.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        accID.setStyle("-fx-alignment: CENTER;");
        opDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        opDate.setStyle("-fx-alignment: CENTER;");
        accType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        accType.setStyle("-fx-alignment: CENTER;");
        branch.setCellValueFactory(new PropertyValueFactory<>("branchID"));
        branch.setStyle("-fx-alignment: CENTER;");

//        table.setItems(accounts);
        filteredObjects = new FilteredList<>(accounts, p -> true);
        sortedObjects  = new SortedList<>(filteredObjects);
        sortedObjects.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedObjects);
        colList = table.getColumns();
        removableList = FXCollections.observableArrayList();
        for(TableColumn<AccountDetailedReport,?> col: colList){
            columnsMap.put(col.getText(), col);
            removableList.add(col.getText());
        }
        columnRemover.setItems(removableList);

        removeColumnButton.setOnAction((actionEvent -> {
            if(columnRemover.getValue()!=null){
                table.getColumns().remove(columnsMap.get(columnRemover.getSelectionModel().getSelectedItem()));
                canBeAdded.getItems().add(columnRemover.getValue());
                removableList.remove(columnRemover.getSelectionModel().getSelectedItem());
                columnRemover.setItems(removableList);
            }
        }));

        addColumnButton.setOnAction((actionEvent -> {
            if(canBeAdded.getSelectionModel().getSelectedItem() != null){
                table.getColumns().add(columnsMap.get(canBeAdded.getSelectionModel().getSelectedItem()));
                removableList.add(canBeAdded.getSelectionModel().getSelectedItem());
                columnRemover.setItems(removableList);
                canBeAdded.getItems().remove(canBeAdded.getSelectionModel().getSelectedItem());
            }
        }));

        reset.setOnAction((actionEvent -> {
            aID.setText("");
            bID.setText("");
            aType.setText("");
            mName.setText("");
            oD.setText("");
        }));
        filteredObjects.predicateProperty().bind(Bindings.createObjectBinding(() ->
                        Account->
                                Account.getAccountType().toLowerCase().contains(aType.getText().toLowerCase()) &&
                                Account.getBranchID().toLowerCase().contains((bID.getText().toLowerCase())) &&
                                Account.getAccountId().toLowerCase().contains((aID.getText().toLowerCase())) &&
                                Account.getMerchantName().toLowerCase().contains(mName.getText().toLowerCase()) &&
                                Account.getDate().toLowerCase().contains(oD.getText().toLowerCase())
                       ,aType.textProperty(),
                bID.textProperty(),
                aID.textProperty(),
                mName.textProperty(),
                oD.textProperty()));
    }
    @FXML
    void removeColumn(ActionEvent event) {

    }

    @FXML
    void addColumn(ActionEvent event) {

    }
}
