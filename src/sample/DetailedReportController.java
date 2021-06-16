package sample;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DetailedReportController {
    DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public ObservableList<Account> accounts = FXCollections.observableArrayList();
    HashMap<String,TableColumn<Account,?>> columnsMap = new HashMap<>();
    FilteredList<Account> filteredObjects;
    SortedList<Account> sortedObjects;
    ObservableList<TableColumn<Account,?>> colList;
    ObservableList<String> removableList;

    DetailedReportController(ObservableList<Account> accounts){
        this.accounts = accounts;
    }
    @FXML
    private ListView<String> canBeAdded;
    @FXML
    private ComboBox<String> columnRemover;
    @FXML
    private TableView<Account> table;
    @FXML
    private TextField aType,bID,aID,oD,mName;
    @FXML
    private TableColumn<Account, String> name,opDate,accType,accID,branch;
//    @FXML
//    private TableColumn<Account,Number> accID,branch;
    @FXML
    private Button reset,removeColumnButton,addColumnButton;

    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory("merchantName"));
        name.setStyle("-fx-alignment: CENTER;");
        accID.setCellValueFactory(new PropertyValueFactory("accountId"));
        accID.setStyle("-fx-alignment: CENTER;");
        opDate.setCellValueFactory(new PropertyValueFactory("date"));
        opDate.setStyle("-fx-alignment: CENTER;");
        accType.setCellValueFactory(new PropertyValueFactory("accountType"));
        accType.setStyle("-fx-alignment: CENTER;");
        branch.setCellValueFactory(new PropertyValueFactory("branchID"));
        branch.setStyle("-fx-alignment: CENTER;");

//        table.setItems(accounts);
        filteredObjects = new FilteredList<>(accounts, p -> true);
        sortedObjects  = new SortedList<>(filteredObjects);
        sortedObjects.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedObjects);
        colList = table.getColumns();
        removableList = FXCollections.observableArrayList();
        for(TableColumn<Account,?> col: colList){
            columnsMap.put(col.getText(), col);
            removableList.add(col.getText());
        }
        columnRemover.setItems(removableList);

        removeColumnButton.setOnAction((actionEvent -> {
            table.getColumns().remove(columnsMap.get(columnRemover.getSelectionModel().getSelectedItem()));
            canBeAdded.getItems().add(columnRemover.getValue());
            removableList.remove(columnRemover.getSelectionModel().getSelectedItem());
            columnRemover.setItems(removableList);
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

//        aID.textProperty().addListener(((observableValue, old, newVal) -> {
////            if(aID.getText()!=null && onlyDigits(aID.getText(),aID.getText().length())){
//            filteredObjects.setPredicate(account -> {
//                if(aID.getText()!=null && onlyDigits(aID.getText(),aID.getText().length())){
//                    return account.getAccountType().toLowerCase().contains(aType.getText().toLowerCase()) &&
//                            account.getAccountId().equals(Integer.parseInt(aID.getText())) &&
//                            account.getMerchantName().toLowerCase().contains(mName.getText().toLowerCase()) &&
//                            account.getDate().toLowerCase().contains(oD.getText().toLowerCase());
//                }
//                return true;
//            });
////            }
//        }));
//        bID.textProperty().addListener(((observableValue, old, newVal) -> {
////            if(aID.getText()!=null && onlyDigits(aID.getText(),aID.getText().length())){
//            filteredObjects.setPredicate(account -> bID.getText() != null && onlyDigits(bID.getText(), bID.getText().length()));
//            sortedObjects = new SortedList<>(filteredObjects);
//            sortedObjects.comparatorProperty().bind(table.comparatorProperty());
//            table.setItems(sortedObjects);
//        }));


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
