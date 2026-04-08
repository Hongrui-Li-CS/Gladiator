package games;

import java.util.ArrayList;

import edu.rutgers.cs112.BST.BSTNode;

/**
 * This class contains methods to represent a Coliseum in Gladiators.
 * It manages cities, people, and the duel process.
 * 
 * @author Kal Pandit
 * @author Maksims Kurjanovics Kravcenko
 * @author Pranay Roni
 */
public class Coliseum {

    private ArrayList<City> cities; // all cities in Gladiators.
    private BSTNode<City> game; // root of the BST. The BST contains cities that are still in the game. 

    /**
     * Default constructor, initializes an empty list of cities.
     */
    public Coliseum() {
        cities = new ArrayList<>();
        game = null; 
    }

    /**
     * Sets up Gladiators, the universe in which the duels takes place.
     * Reads cities and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupGladiators(String filename) {
        StdIn.setFile(filename); // open the file - done only once here
        setupCities();
        setupPeople();
    }

    /**
     * Reads the following from input file:
     * - Number of cities
     * - city IDs
     * Add cities into the cities ArrayList in order of appearance. 
     */
    public void setupCities() {
        int numCities = StdIn.readInt(); 
        cities = new ArrayList<>();

        for(int i = 0; i < numCities; i ++){
             int id = StdIn.readInt(); 
             City city = new City(id);
             cities.add(city);
        }


    }

    /**
     * Reads the following from input file:
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, city id, effectiveness 
     * 
     * You should add each person to the corresponding city based on their city id.
     * If the person's birth month is odd, add to oddPopulation, else add to evenPopulation.
     */
    public void setupPeople() { 
        int numPeople = StdIn.readInt();
        for(int i = 0; i < numPeople; i++){
             String first = StdIn.readString();
            String last  = StdIn.readString();
            int month    = StdIn.readInt();
            int age      = StdIn.readInt();
            int cityId   = StdIn.readInt();
            int eff      = StdIn.readInt();

            Person person = new Person(first, last, month, age, cityId, eff);

            City city = null;
        for (City c : cities) {
            if (c.getCityNumber() == cityId) {
                city = c;
                break;
            }
        }

        if (month % 2 == 1) {
            city.getOddPopulation().add(person);
        } else {
            city.getEvenPopulation().add(person);
        }

        }
 
    }

    /**
     * Adds a city to the game BST.
     * If the city is already added, do nothing
     *  
     * @param newCity the city we wish to add
     */
    public void addCityToGame(City newCity) {
         game = addCityToBST(game, newCity);
 
    }

    private BSTNode<City> addCityToBST(BSTNode<City> root, City newCity) {
    
        if (root == null) {
            return new BSTNode<City>(newCity);
        }

        int cmp = newCity.compareTo(root.getData());

        if (cmp == 0) {
        
            return root;
        } else if (cmp < 0) {
        
            root.setLeft(addCityToBST(root.getLeft(), newCity));
        } else { 
        
            root.setRight(addCityToBST(root.getRight(), newCity));
        }

        return root;
    }

    /**
     * Searches for a city inside of the BST given the city id.
     * 
     * @param id the city to search
     * @return the city if found, null if not found
     */
    public City findCity(int id) {
        return findCityHelper(game, id);
    }

    private City findCityHelper(BSTNode<City> root, int id) {
    
        if (root == null) {
            return null;
        }

        City curCity = root.getData();
        int curId = curCity.getCityNumber();

        if (id == curId) {
        
            return curCity;
        } else if (id < curId) {
        
            return findCityHelper(root.getLeft(), id);
        } else {
        
            return findCityHelper(root.getRight(), id);
        }
    }

   
    /**
     * Selects two duelers from the tree, according to a series of selection rules.
     * View the assignment description for exact implementation details.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
        Person person1 = null; // odd dueler
        Person person2 = null; // even dueler


        int[] cityId1 = new int[]{-1}; // city of person1
        int[] cityId2 = new int[]{-1}; // city of person2

        person1 = selectOddYoung(game, cityId1);

        int forbiddenForEvenYoung = (person1 == null) ? -1 : cityId1[0];
        person2 = selectEvenYoung(game, cityId2, forbiddenForEvenYoung);

        if (person1 == null) {
            int forbiddenForRandomOdd = (person2 == null) ? -1 : cityId2[0];
            person1 = selectRandomOdd(game, cityId1, forbiddenForRandomOdd);
        }

        if (person2 == null) {
            int forbiddenForRandomEven = (person1 == null) ? -1 : cityId1[0];
            person2 = selectRandomEven(game, cityId2, forbiddenForRandomEven);
        }

        DuelPair pair = new DuelPair();
        pair.setPerson1(person1);
        pair.setPerson2(person2);
        return pair;
    }
// helper 1, find youngWarrior in OddPopulation
    private Person selectOddYoung(BSTNode<City> root, int[] chosenCityId) {
        if (root == null) return null;

        City city = root.getData();
        int curId = city.getCityNumber();

        ArrayList<Person> odds = city.getOddPopulation();
        for (int i = 0; i < odds.size(); i++) {
            Person p = odds.get(i);
            if (p.isYoungWarrior()) {       
                odds.remove(i);             
                chosenCityId[0] = curId;   
                return p;
            }
        }

        
        Person left = selectOddYoung(root.getLeft(), chosenCityId);
        if (left != null) return left;

        return selectOddYoung(root.getRight(), chosenCityId);
    }
  
// helper 2, find youngWarrior in evenPopulation
     private Person selectEvenYoung(BSTNode<City> root, int[] chosenCityId, int forbiddenCityId) {
        if (root == null) return null;

        City city = root.getData();
        int curId = city.getCityNumber();

        if (curId != forbiddenCityId) {
            ArrayList<Person> evens = city.getEvenPopulation();
            for (int i = 0; i < evens.size(); i++) {
                Person p = evens.get(i);
                if (p.isYoungWarrior()) {
                    evens.remove(i);
                    chosenCityId[0] = curId;
                    return p;
                }
            }
        }

        Person left = selectEvenYoung(root.getLeft(), chosenCityId, forbiddenCityId);
        if (left != null) return left;

        return selectEvenYoung(root.getRight(), chosenCityId, forbiddenCityId);
    }

// helper 3, if there is no odd young, pick odd randomly
    private Person selectRandomOdd(BSTNode<City> root, int[] chosenCityId, int forbiddenCityId) {
        if (root == null) return null;

        City city = root.getData();
        int curId = city.getCityNumber();

        if (curId != forbiddenCityId) {
            ArrayList<Person> odds = city.getOddPopulation();
            if (!odds.isEmpty()) {
                int idx = StdRandom.uniform(odds.size()); 
                Person p = odds.remove(idx);
                chosenCityId[0] = curId;
                return p;
            }
        }

        Person left = selectRandomOdd(root.getLeft(), chosenCityId, forbiddenCityId);
        if (left != null) return left;

        return selectRandomOdd(root.getRight(), chosenCityId, forbiddenCityId);
    }
//helper 4, if there is no even young, pick even randomly

 private Person selectRandomEven(BSTNode<City> root, int[] chosenCityId, int forbiddenCityId) {
        if (root == null) return null;

        City city = root.getData();
        int curId = city.getCityNumber();

        if (curId != forbiddenCityId) {
            ArrayList<Person> evens = city.getEvenPopulation();
            if (!evens.isEmpty()) {
                int idx = StdRandom.uniform(evens.size()); 
                Person p = evens.remove(idx);
                chosenCityId[0] = curId;
                return p;
            }
        }

        Person left = selectRandomEven(root.getLeft(), chosenCityId, forbiddenCityId);
        if (left != null) return left;

        return selectRandomEven(root.getRight(), chosenCityId, forbiddenCityId);
    }


    /**
     * Removes a city from the BST given the city id.
     * 
     * @param id the city to eliminate
     */
    public void eliminateCity(int id) {
        game = eliminateCityHelper(game, id);
    }


    private BSTNode<City> eliminateCityHelper(BSTNode<City> root, int id) {
        if (root == null) {
            return null;
        }

        int curId = root.getData().getCityNumber();

        if (id < curId) {
        
            root.setLeft(eliminateCityHelper(root.getLeft(), id));
            return root;
        } else if (id > curId) {
        
            root.setRight(eliminateCityHelper(root.getRight(), id));
            return root;
        } else {
        

        
        if (root.getLeft() == null && root.getRight() == null) {
            return null;
        }

        
        if (root.getLeft() == null) {
            return root.getRight();
        }

        
        if (root.getRight() == null) {
            return root.getLeft();
        }

       
        BSTNode<City> succ = root.getRight();
        while (succ.getLeft() != null) {
            succ = succ.getLeft();
        }

        City curCity  = root.getData();
        City succCity = succ.getData();

        
        curCity.setCityNumber(succCity.getCityNumber());
        curCity.setOddPopulation(succCity.getOddPopulation());
        curCity.setEvenPopulation(succCity.getEvenPopulation());

       
        root.setRight(eliminateCityHelper(root.getRight(), succCity.getCityNumber()));

        return root;
        }
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets added back to their city
     * - If a cities odd OR even population is empty, eliminate it from the game 
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
         if (pair == null) return;

        Person p1 = pair.getPerson1();
        Person p2 = pair.getPerson2();

    
        if (p1 == null && p2 == null) {
            return;
        }

        if (p1 == null || p2 == null) {
        
            Person solo = (p1 != null) ? p1 : p2;
            City city = findCity(solo.getCityNumber());
            if (city != null) {
                if (solo.getBirthMonth() % 2 == 1) {
                    city.addOddPerson(solo);
                } else {
                    city.addEvenPerson(solo);
                }
            }
            return;   
        }

    
        Person winner = p1.duel(p2);
        Person loser  = (winner == p1) ? p2 : p1;

    
        City winnerCity = findCity(winner.getCityNumber());
        if (winnerCity != null) {
            if (winner.getBirthMonth() % 2 == 1) {
                winnerCity.addOddPerson(winner);
            } else {
                winnerCity.addEvenPerson(winner);
            }
        }

    
        City loserCity = findCity(loser.getCityNumber());

    
        if (winnerCity != null) {
            int oddSize  = winnerCity.getOddPopulation().size();
            int evenSize = winnerCity.getEvenPopulation().size();
            if (oddSize == 0 || evenSize == 0) {
                eliminateCity(winnerCity.getCityNumber());
            }
        }

    
        if (loserCity != null) {
            int oddSize  = loserCity.getOddPopulation().size();
            int evenSize = loserCity.getEvenPopulation().size();
            if (oddSize == 0 || evenSize == 0) {
                eliminateCity(loserCity.getCityNumber());
            }
        }

    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of cities for the Driver.
     * 
     * @return the ArrayList of cities for selection
     */
    public ArrayList<City> getCities() {
        return this.cities;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public BSTNode<City> getRoot() {
        return game;
    }
}