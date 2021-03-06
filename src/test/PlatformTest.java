package test;

import collection.Card;
import collection.CollectionOwn;
import dao.*;
import org.junit.jupiter.api.*;
import platform.Platform;
import userSide.Exchange;
import userSide.User;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlatformTest {

    private Platform platform = Platform.getInstance();
    private User testUser = new User("nomeProva"+randomStringGeneratore(), "cognomeProva"+randomStringGeneratore(), "usernameProva"+randomStringGeneratore(), "emailProva"+randomStringGeneratore()+"@test.com");;
    private String passwordTest="1234";
    private CollectionOwn cardsOwn;
    private Map<Card,Integer> cardsWanted;


    /**
     * Test signup method creating an user and checking if his collection can be gotten , generated randomly, from database
     */
    @Test
    @Order(1)
    public void signupTest(){
        try {
            Boolean result = this.platform.signUp(testUser.getNome(), testUser.getCognome(), testUser.getUsername(), testUser.getEmail(), passwordTest, "retype");
            cardsOwn = new CollectionOwnDaoImpl().getCollentionOwn(testUser);
            assertTrue(result && (cardsOwn!=null));
        }
        catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test login method trying access with an user and checking if his collection is correctly found in database
     */
    @Test
    @Order(2)
    public void loginTest(){
        try {
            String secretkey = "chiavesupersegretissimaXD";
            String pass = Platform.encrypt(passwordTest, secretkey);
            CollectionOwn testCollection = this.platform.LogIn("Obe", pass);

            Facade daoFacade = new FacadeImplements();
            User testUser = daoFacade.findByUsername("Obe");
            Map<Card,Integer> collectionOwn = daoFacade.getCollentionOwn(testUser).getCardsOwn();
            boolean equalCollResult = true;
            boolean checkedCard = false;
            for (Card collToTest : testCollection.getCardsOwn().keySet()
                 ) {
                for (Card collection : collectionOwn.keySet()
                     ) {
                    boolean cond1 = collection.getId() == collToTest.getId();
                    boolean cond2 = testCollection.getCardsOwn().get(collToTest) != collectionOwn.get(collection);
                    if (cond1 && cond2){
                        checkedCard = true;
                        equalCollResult=false;
                    }
                    if (checkedCard){
                        equalCollResult=false;
                    }
                }
            }
            assertTrue(equalCollResult);
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    /**
     *Tests set exchange method to see if a user can set an exchange
     */
    @Test
    @Order(3)
    public void setExchangeTest(){
        ArrayList<Integer> cardsToGive = setTestCards(1);
        ArrayList<Integer> cardsToTake = setTestCards(5);
        try {
            int idExchange = this.platform.setExchange("Obe", cardsToGive, cardsToTake);
            if(idExchange ==0){
                fail("setting exchange test failed because idExchange returned is 0");
            }
            this.platform.getExchange(idExchange);
            //se non si rompe nel riprendermi dal DB lo scambio settato, posso ritenerlo settato con successo


        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test the case when an user accept an exchange checking if method returns true value
     */
    @Test
    @Order(4)
    public void acceptExchangeTest(){
        try {

            ArrayList<Integer> cardsToGive = new ArrayList<>();// = setTestCards(1);
            ArrayList<Integer> cardsToTake = new ArrayList<>();// = setTestCards(5);

            ExchangeCardDAOImpl exchangeCardDAO = new ExchangeCardDAOImpl();
            UserDaoImpl userDao = new UserDaoImpl();
            User testUserSetExchange = userDao.findByUsername("Obe");
            User testUserAccepting = userDao.findByUsername("Pol");

            CollectionOwnDao collectionOwnDao = new CollectionOwnDaoImpl();
            for (Card c : collectionOwnDao.getCollentionOwn(testUserSetExchange).getCardsOwn().keySet()
                 ) {
                cardsToGive.add(c.getId());
                if (cardsToGive.size()>=5)
                    break;
            }

            for (Card c : collectionOwnDao.getCollentionOwn(testUserAccepting).getCardsOwn().keySet()
            ) {
                cardsToTake.add(c.getId());
                if (cardsToTake.size()>=5)
                    break;
            }
            int idExchange = this.platform.setExchange(testUserSetExchange.getUsername(), cardsToGive, cardsToTake);
            Exchange exchangeid= exchangeCardDAO.getExchange(idExchange);
            boolean result = this.platform.marketExchange(exchangeid, testUserAccepting.getUsername());
            assertTrue(result);

        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }


    /**
     * method used to generate an user without redundant issue that can be used to test
     * @return the user will be used to Test
     */
    private User getTestUser(){
        User tempUser = new User("nomeProva"+randomStringGeneratore(), "cognomeProva"+randomStringGeneratore(), "emailProva"+randomStringGeneratore()+"@test.com", "passwordTest"+randomStringGeneratore());
        return testUser;
    }

    /**
     * method to generate a random string
     * @return string generated randomly
     */
    private String randomStringGeneratore(){
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();

            return generatedString;
    }

    /**
     * method to set 5 sorted cards in arraylist
     * @param i Index of first card
     * @return The list of cards
     */
    private ArrayList<Integer> setTestCards(int i){
        ArrayList<Integer> setList = new ArrayList<>();
        int range = i+5;
        for(; i<range; i++){
            setList.add(i);
        }
        return setList;
    }
    public String getPasswordTest() {
        return passwordTest;
    }

    public void setPasswordTest(String passwordTest) {
        this.passwordTest = passwordTest;
    }
}

