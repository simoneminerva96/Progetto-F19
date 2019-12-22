package dao;

import collection.Card;
import userSide.User;

//import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CollectionOwnDaoImpl implements CollectionOwnDao {
    /**
     * query used to select an exchange in DB
     */
    private static final String VIEW_COLLECTION_QUERY = "select * from collections inner join catalog on (collections.ID_Card = catalog.ID) AND Username = ?";

    private static final String INSERT_QUERY = "INSERT INTO collections (ID_Card, Username, In_Market)"+"VALUES";

    private static final String new_randow_card_for_new_user ="insert into collections(ID_Card,USERNAME) values ((select id from catalog order by rand() limit 1),?)";

    private static final String get_last_card_sachet = "select * from collections inner join catalog on (collections.ID_Card = catalog.ID) WHERE  Username = ? order by IDCard desc limit 1";

    private static final String HAS_CARDS_QUERY ="select * from collections where ID_Card = ?, ID_User = ?";



    MySQLDAOFactory connector = MySQLDAOFactory.getInstance();
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet result = null;


    public boolean insert(User user, Card card, boolean in_market) {
        conn = null;
        try {
            conn = connector.createConnection();
            String query2 = INSERT_QUERY + "("+user.getUsername()+", "+card.getId()+", "+in_market+")";
            preparedStatement = conn.prepareStatement(query2);
            preparedStatement.execute();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                result.close();
            } catch (Exception rse) {
                rse.printStackTrace();
            }
            try {
                preparedStatement.close();
            } catch (Exception sse) {
                sse.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception cse) {
                cse.printStackTrace();
            }
        }
        return false;

    }


    @Override
    public boolean update() throws SQLException {
        return false;
    }

    @Override
    public boolean delete() throws SQLException {
        return false;
    }

    @Override
    public Map<Card,Integer> getCollentionOwn(User user){
        Map<Card,Integer> c=new HashMap<>();
        //String listaCarte = VIEW_COLLECTION_QUERY;
        user.getUsername();
        try {
            conn = connector.createConnection();

            preparedStatement = conn.prepareStatement(VIEW_COLLECTION_QUERY);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.execute();
            result = preparedStatement.getResultSet();

            while (result.next() && result != null) {
                Card card=new Card(result.getInt("ID"),
                        result.getString("Category"),
                        result.getString("Class"),
                        result.getInt("Lvl"),
                        result.getString("Rarity"),
                        result.getString("CardType"),
                        result.getString("CardName"),
                        result.getString("CardDescription"));
                        c.put(card,result.getInt("quantity"));
                //to do: farla diventare mappa con quantità
            }
            return c;
        }catch (SQLException e){
            return null;
        }
    }

    @Override
    public Card createRandomCard(User user){
        try{
            Card card;
            conn = connector.createConnection();
            preparedStatement = conn.prepareStatement(new_randow_card_for_new_user);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.execute();
            card = get_last_card(user);
            return card;
        }catch (SQLException e){
            System.out.println(e.toString());
            return null;
        }

    }

    @Override
    public  Card get_last_card(User user){
        try {
            conn = connector.createConnection();

            preparedStatement = conn.prepareStatement(get_last_card_sachet);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.execute();
            result = preparedStatement.getResultSet();

            Card card=new Card(result.getInt("ID"),
                    result.getString("Category"),
                    result.getString("Class"),
                    result.getInt("Lvl"),
                    result.getString("Rarity"),
                    result.getString("CardType"),
                    result.getString("CardName"),
                    result.getString("CardDescription"));
            return card;
        }catch (SQLException e){
            return null;
        }
    }



    /**
     * Method that allows the logged user to find certain cards in his collection,
     * through the filters of the search bar.
     * <p>
     *      depending to parameters specified, the original query is completed in such a way that
     *      it can returned the requested cards
     * </p>
     * @param user type User. Indicates logged user
     * @param name  a String. Indicates name of the card searched.
     * @param category  a String. Indicates category of the card searched.
     * @param classCard a String. Indicates class of the card searched .
     * @param typeCard  a String. Indicates type of the card searched.
     * @return ArrayList<Card> that contains the requested cards from logged user.
     */
    public ArrayList<Card> filters (User user, String name, String category , String classCard, String typeCard ){

        ArrayList<Card> list= new ArrayList<Card>();
        int j=2;
        String search_cards="select * from collections inner join catalog on (collections.ID_Card=catalog.ID) WHERE Username=?";
        try {
            conn = connector.createConnection();
            if( !name.equals("")|| category!=null || !classCard.equals("") || !typeCard.equals("")) {

                if(!name.equals("")){
                    search_cards+=" AND CardName=?";
                }
                if(category!=null){
                    search_cards+=" AND Category=?";
                }
                if(!classCard.equals("")){
                    search_cards+=" AND Class=?";
                }
                if(!typeCard.equals("")){
                    search_cards+=" AND CardType =?";

                }
                preparedStatement = conn.prepareStatement(search_cards);
                preparedStatement.setString(1, user.getUsername());
                if(!name.equals("")){
                    preparedStatement.setString(j,name);
                    j++;
                }
                if(category!=null){
                    preparedStatement.setString(j,category);
                    j++;
                }
                if(!classCard.equals("")){
                    preparedStatement.setString(j,classCard);
                    j++;
                }
                if(!typeCard.equals("")){
                    preparedStatement.setString(j,typeCard);
                    j++;
                }
            }
            preparedStatement.execute();
            result = preparedStatement.getResultSet();
            while (result.next() && result != null) {
                for (int i = 0; i < result.getInt("quantity"); i++) {
                    Card answer = new Card(result.getInt("ID"),
                            result.getString("Category"),
                            result.getString("Class"),
                            result.getInt("Lvl"),
                            result.getString("Rarity"),
                            result.getString("CardType"),
                            result.getString("CardName"),
                            result.getString("CardDescription"));
                    list.add(answer);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                result.close();
            } catch (Exception rse) {
                rse.printStackTrace();
            }
            try {
                preparedStatement.close();
            } catch (Exception sse) {
                sse.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception cse) {
                cse.printStackTrace();
            }
        }
        return list;
    }





    @Override
    public ArrayList<Card> openSachet(User user){
        ArrayList<Card> c = new ArrayList<Card>();

        for(int i=0; i<6; i++){
            c.add(createRandomCard(user));
        }
        return c;
    }

    //metodo per controllare che possegga le carte inseritegli
    public boolean hasCards(int userID, ArrayList<Card> cards){

        try {
            conn = connector.createConnection();
            //TODO fare in modo che vengo eseguito per ogni carta dell' arraylist, meglio prendere tutte le carte con una query e operare poi con un ciclo
            preparedStatement = conn.prepareStatement(HAS_CARDS_QUERY);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
