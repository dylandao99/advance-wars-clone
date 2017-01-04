/*//-----------------------------------------------------------------------
Author: Dylan Dao
Date: Tuesday, January 13, 2014
Purpose: An array-based game (stripped-down version of Advance Wars) made to
	 teach usage and applications of arrays in coding for use on
	 programming instruction webside code.org
*/ //-----------------------------------------------------------------------

//IMAGES SOURCES
/*-----------------------------------------------------------------------
Comic Text Pictures - cooltext.com
Map Pieces - http://www.portablegaming.de/attachments/spiele-nintendo-ds/9288d1158444001-advance-wars-dual-strike-map-thread-kontakt.png
	     http://critical-gaming.com/storage/advance_wars_chess_map.png?__SQUARESPACE_CACHEVERSION=1302646116904
	     http://lparchive.org/Advance-Wars-2/Update%2007/15-06_day01_6.png
All Character Sprites - http://spritedatabase.net/file/2315
Stars - http://dreamscapehomebuilders.com/wp-content/uploads/2014/05/5-stars.jpg
-----------------------------------------------------------------------*/

//AUDIO SOURCES
/*-----------------------------------------------------------------------
hfcyg.wav - http://www.newgrounds.com/audio/listen/599787
step1.wav - http://www.newgrounds.com/audio/listen/572661
step3.wav - http://www.newgrounds.com/audio/listen/572663
watch_beep_soft.wav - www.soundswap.org
-----------------------------------------------------------------------*/

//imports
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import sun.audio.*;

//class kaboom
public class kaboom extends Applet implements ActionListener
{
    CardLayout card = new CardLayout (); //card layout to switch game screens
    Panel game = new Panel (); //panel for entire game

    //audio objects
    static AudioPlayer MGP = AudioPlayer.player;
    static AudioStream BGM;
    static AudioData MD;
    static ContinuousAudioDataStream loop = null;

    //define grid dimensions
    int col = 30;
    int row = 20;

    //intro & tutorial script
    String intro[] = {"Welcome soldier!",
	"You've been promoted to tactical advisor.",
	"Today's battle is crucial.", "From here on out you'll be leading our troops to victory.",
	"Make us proud."};

    String tut[] = {"Your goal is to capture the enemy base. This is done by strategically commanding your units (unit loyalty shown by color of unit).",
	"The battle is divided into phases. On your phase, each unit has a single turn. Once you click END PHASE, the opponent's phase begins and so forth. On that turn you may move and attack if an enemy is within your attack range. Depending on the type of unit, attack and movement range differ.",
	"Keep in mind the following. Terrain with high defense restrict unit movement. Vehicles cannot traverse through water and all units can only attack enemy units 1 square away save for the Missile Unit that can attack anywhere on the board without being countered unless opposing unit is also a Missile Unit.",
	"A unit's health represents its strength in numbers. If it reaches 0, that unit dies.",
	"Several factors affect the result of any given battle. ",
	"During combat, both units attack each other, the attacking unit having an offensive advantage.",
	"Depending on the terrain type that a unit is standing on, the unit receives a corresponding defense increase depending on the number of stars a terrain is rated.",
	"Additionally, each unit has their respective advantages and disadvantages. Furthermore, the damage a unit does depends on its health.",
	"Buildings on the map also have their own significances. If a unit is at any of your buildings at the beginning of a turn, the unit will receive a health recovery. ",
	"Factories allow you to spawn units given you have enough income. To spawn a unit, click on a factory that you own, choose a unit, and choose an adjacent square. Each factory may only spawn one unit per phase.",
	"Cities provide income at the start of each turn.", "Buildings affiliation is signified by the color of the building. Grey buildings are neutral territory.", "Both neutral and enemy buildings may be captured. ",
	"To capture a building, an INFANTRY unit must be on a building square at the beginning of a turn. Depending on the health of the unit, this process can span over many turns before the building is captured.",
	"Naturally, enemy buildings take more turns to be captured than neutral buildings do. If a building becomes unoccupied during capture, capture progress resets. All buildings, including bases, are captured in the same fashion.",
	"Buttons with (*character*) in the label mean that they can be invoked through pressing the corresponding key. Also, DO NOT resize windows or close dialogs with X buttons!",
	"At any time during a player turn (without factory or action menu dialog open), a game can be saved and later loaded from the main menu.",
	"That should be all. Good luck."};

    //script display index
    int nScript = 0;

    //script displays
    JTextArea dIntro;
    JLabel tutPic;
    JButton next;
    JButton back;

    //button grid array
    JButton mapButton[] = new JButton [600];

    //determines map layout/terrain type
    int mapData[] [];

    //unit team affiliation (0 = neutral/void, 1 = red, 2 = blue)
    int uAfil[] [] = new int [row] [col];

    //unit type (0 = N/A, 1 = infantry, 2 = bazooka, 3 = tank, 4 = missile)
    int uType[] [] = new int [row] [col];

    //unit state (0 = not active or waiting, 1 = ready)
    int uState[] [] = new int [row] [col];

    //unit health (1-10)
    int uHealth[] [] = new int [row] [col];

    //unit display info
    String uName[] = {"", "Infantry", "Bazooka", "Tank", "Missile"}; //name by type index
    int uPrice[] = {0, 500, 800, 1000, 1200}; //price by type index
    int uAdv[] = {0, 2, 3, 1, 0}; //unit advantages by type index
    int uDis[] = {0, 3, 1, 2, 0}; //unit disvantages by type index
    int uMov[] = {0, 4, 3, 5, 2}; //unit movement range by type index


    //terrain display info
    String tName[] = {"Grass", "Forest", "Road", "Mountain", "City", "Water", "Neutral Factory", "Red Factory", "Blue Factory", "Red Base", "Blue Base", "Red City", "Blue City"}; //name by type index
    String afilCode[] = {"Neutral", "Red", "Blue", "Capturable"}; //affiliation by type index
    int tDef[] = {1, 2, 1, 3, 4, 3, 4, 4, 4, 5, 5, 4, 4}; //defense by type index
    int tAfil[] = {0, 0, 0, 0, 3, 0, 3, 1, 2, 1, 2, 1, 2}; //terrain affiliation by type index

    //building stength tracker
    int bStrength[] [] = new int [row] [col];

    //displays which team's turn it is
    JLabel dTurn;
    int turn = 0;

    //displays overall team info
    JLabel dIncome;
    JLabel dNum;
    JLabel dBuilding;
    int income[] = new int [2];
    int uNum[] = new int [2];
    int building[] = new int [2];

    //displayed terrain information
    JLabel jType;
    JLabel jPic;
    JLabel jAfil;
    JLabel jDef;
    JLabel jHealth;
    JLabel jLoc;

    //displayed unit information in main Game Screen
    JLabel iPic;
    JLabel iType;
    JLabel iTeam;
    JLabel iHealth;
    JLabel iAdv;
    JLabel iDis;
    JLabel iMov;

    //factory unit information
    JLabel iPic2;
    JLabel iType2;
    JLabel iPrice2;
    JLabel iAdv2;
    JLabel iDis2;
    JLabel iMov2;

    //pop-up dialogs
    JFrame factory;
    JFrame action;
    JFrame bLog;
    JFrame tLog;

    //factory widgets
    JComboBox drop;
    String sel[] = {"Infantry - $500", "Bazooka - $800", "Tank - $1000", "Missile - $1200"}; //dropdown box selections
    JButton confirm;

    //store selected location
    int loc = 0;

    //factory variables
    JComboBox facCB;
    String facS;
    int facChoice;

    //action menu buttons
    JButton move;
    JButton attack;
    JButton wait;
    JButton cancel;

    //battle log components
    JTextArea bLogTA;
    JTextArea tLogTA;
    JLabel tTitle;
    JButton cont;
    JButton cont2;

    //unit move variables
    boolean moving = false;
    int movLoc = 0;
    boolean moved = false;

    //bottom buttons
    JButton endP;
    JButton save;
    JButton mm;

    //timer
    Timer timer;
    JLabel dTimerH;
    JLabel dTimerM;
    JLabel dTimerS;
    int hour = 0;
    int minute = 0;
    int second = 0;

    //timer win screen
    JLabel dTimerH2;
    JLabel dTimerM2;
    JLabel dTimerS2;

    //win screen
    JLabel dWinner;
    JLabel dFlag;
    JLabel dCasualties;
    int casualties = 0;
    JLabel dnTurn;
    int nTurn = 0;

    //grid for Dijkstra pathfinding
    int movGrid[] [] = new int [row] [col];

    //is attacking, limits button actions
    boolean attacking = false;

    public void init ()  //init method
    {
	playMusic ("hfcyg");

	resetGame ();

	resize (1250, 800);
	game.setLayout (card);

	addMM ();
	addIntro ();
	addGS ();
	addWinScreen ();

	addFactory ();
	addAction ();
	addBattleLog ();
	addTurnLog ();

	initBuildings ();

	add ("Center", game);
    } //end init


    public void addMM ()  //add main menu
    {
	setBackground (new Color (221, 174, 107)); //set background color to beige

	//panels to organize and place widgets
	Panel p = new Panel (); //main panel
	Panel p2 = new Panel ();
	Panel p3 = new Panel ();
	Panel p4 = new Panel ();
	Panel p5 = new Panel ();

	//set Boxlayout to main panel, layout sorts widgets along y-axis center-aligned while keeping individual widget sizes
	BoxLayout bl = new BoxLayout (p, BoxLayout.Y_AXIS);
	p.setLayout (bl);

	//label displays
	JLabel title = new JLabel (createImageIcon ("title.png"));

	JLabel des = new JLabel ("A not-so-subtle fanmade Advance Wars replica!");

	JLabel bgi = new JLabel (createImageIcon ("mm_background.png"));
	bgi.setBorder (BorderFactory.createLineBorder (Color.black, 5)); //set picture border

	//grid layout panel for buttons
	Panel g = new Panel (new GridLayout (3, 1, 3, 3));

	//buttons
	JButton play2 = new JButton (createImageIcon ("2player.png"));
	play2.addActionListener (this);
	play2.setActionCommand ("play");

	JButton load = new JButton (createImageIcon ("load.png"));
	load.addActionListener (this);
	load.setActionCommand ("load");

	JButton quit = new JButton (createImageIcon ("quit.png"));
	quit.addActionListener (this);
	quit.setActionCommand ("quit");

	//add widgets to respective panels
	p2.add (title);

	p3.add (des);

	p4.add (bgi);

	g.add (play2);
	g.add (load);
	g.add (quit);
	p5.add (g);

	//add widgets to main panel
	p.add (p2);
	p.add (p3);
	p.add (p4);
	p.add (p5);

	//add main panel to 1st slot in game panel
	game.add ("1", p);
    } //end addMM


    public void addIntro ()  //add introduction + tutorial
    {
	//panels to organize and place widgets
	Panel p = new Panel (); //main panel
	Panel p2 = new Panel (new BorderLayout ()); //sort objects according to directional borders of screen

	//holds buttons at bottom of screen
	Panel bottom = new Panel ();

	//tutorial picture
	tutPic = new JLabel (createImageIcon ("tut_17.png")); //set initial
	tutPic.setBorder (BorderFactory.createLineBorder (Color.black, 1));

	//intro/tutorial text display widget
	dIntro = new JTextArea ("\n\n\n\n\n\n\n\n\n\n\n\n"); //set inital, sizing
	dIntro.setEditable (false);
	dIntro.setFont (new Font ("Consolas", Font.PLAIN, 20));
	dIntro.setLineWrap (true);
	dIntro.setWrapStyleWord (true);

	back = new JButton ("<<BACK");
	back.addActionListener (this);
	back.setActionCommand ("back");

	next = new JButton ("NEXT>>");
	next.addActionListener (this);
	next.setActionCommand ("next");

	bottom.add (back);
	bottom.add (next);

	//add to directional borders in p2 panel
	p2.add (tutPic, BorderLayout.NORTH);
	p2.add (dIntro, BorderLayout.CENTER);
	p2.add (bottom, BorderLayout.SOUTH);

	p.add (p2); //add to main panel

	game.add ("2", p); //add main panel to 2st slot in game panel
    } //end addIntro


    public void addGS ()  //add game screen
    {
	//panels to sort and place widgets
	Panel p = new Panel (new BorderLayout ());
	Panel p2 = new Panel ();
	Panel p3 = new Panel (new BorderLayout ());

	//organize widgets by category
	Panel left = new Panel (new GridLayout (4, 1));
	Panel right = new Panel ();
	Panel iTerrain = new Panel ();
	Panel iUnit = new Panel ();
	Panel bottom = new Panel ();
	Panel time = new Panel ();

	//add y-axis alignment to certain panels
	BoxLayout bl = new BoxLayout (right, BoxLayout.Y_AXIS);
	right.setLayout (bl);

	BoxLayout bl2 = new BoxLayout (iTerrain, BoxLayout.Y_AXIS);
	iTerrain.setLayout (bl2);

	BoxLayout bl3 = new BoxLayout (iUnit, BoxLayout.Y_AXIS);
	iUnit.setLayout (bl3);

	//timer widgets
	//------------------------------
	JLabel timTimer = new JLabel ("Game Time: ");
	timTimer.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dTimerH = new JLabel ("00"); //hour display
	dTimerH.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel sm = new JLabel (":");
	sm.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dTimerM = new JLabel ("00"); //minute display
	dTimerM.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel sm2 = new JLabel (":");
	sm2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dTimerS = new JLabel ("00"); //second display
	dTimerS.setFont (new Font ("MS Gothic", Font.PLAIN, 20));
	//------------------------------

	//Team Info widgets
	//------------------------------
	dTurn = new JLabel (createImageIcon ("turn_" + turn + ".png")); //display turn

	JLabel lTitle = new JLabel ("TEAM INFO");
	lTitle.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 30));

	dBuilding = new JLabel ("Building Count: " + building [turn]);
	dBuilding.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dIncome = new JLabel ("Income: $" + income [turn]);
	dIncome.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dNum = new JLabel ("Unit Count: " + uNum [turn]);
	dNum.setFont (new Font ("MS Gothic", Font.PLAIN, 20));
	//------------------------------

	//Terrain Info widgets
	//------------------------------
	JLabel tTitle = new JLabel ("TERRAIN INFO");
	tTitle.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 30));

	jPic = new JLabel (createImageIcon ("map_0_0_0_0.gif")); //display terrain picture
	jPic.setBorder (BorderFactory.createLineBorder (Color.black, 1));
	jPic.setPreferredSize (new Dimension (30, 30));

	jType = new JLabel ("Type: -----------------"); //type of terrain, sizing widget
	jType.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	jLoc = new JLabel ("Location: -------------------"); //coordinate location of terrain, sizing widget
	jLoc.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	jAfil = new JLabel ("Team: -----------------"); //affiliation of terrain, sizing widget
	jAfil.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	jHealth = new JLabel ("Capture Strength:             "); //capture strength of terrain, sizing widget
	jHealth.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	JLabel defTitle = new JLabel ("Defense: ");
	defTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	jDef = new JLabel (createImageIcon ("stars_0.png")); //defense rate of terrain, sizing widget, icon
	//------------------------------

	//Unit Info
	//------------------------------
	JLabel uTitle = new JLabel ("UNIT INFO");
	uTitle.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 30));

	iPic = new JLabel (createImageIcon ("map_0_0_0_0.gif")); //unit animated .gif, sizing widget
	iPic.setPreferredSize (new Dimension (30, 30));

	iType = new JLabel ("Type: -----------------"); //unit type, sizing widget
	iType.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	iTeam = new JLabel ("Team: -----------------"); //unit team, sizing widget
	iTeam.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	JLabel advTitle = new JLabel ("Advantage: ");
	advTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iAdv = new JLabel (createImageIcon ("blank.gif")); //unit advantage, sizing widget, animated .gif
	iAdv.setPreferredSize (new Dimension (30, 30));

	JLabel disTitle = new JLabel ("Disadvantage: ");
	disTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20)); //unit disadvantage, sizing widget, animated .gifv

	iDis = new JLabel (createImageIcon ("blank.gif"));
	iDis.setPreferredSize (new Dimension (30, 30));

	JLabel hTitle = new JLabel ("Health: ");
	hTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iHealth = new JLabel (createImageIcon ("health_1.png")); //unit health, sizing widget, icon

	JLabel mTitle = new JLabel ("Movement Range: ");
	mTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iMov = new JLabel (createImageIcon ("stars_0.png")); //unit health, sizing widget, icon
	//------------------------------

	//Bottom buttons
	//------------------------------
	endP = new JButton ("(p) END PHASE");
	endP.addActionListener (this);
	endP.setActionCommand ("endP");
	endP.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen for key 'p' when window is active
	    .put (KeyStroke.getKeyStroke ('p'), "pressed p");
	endP.getActionMap ().put ("pressed p", key_p); //do action key_p when 'p' is pressed

	save = new JButton ("SAVE GAME");
	save.addActionListener (this);
	save.setActionCommand ("save");

	mm = new JButton ("MAIN MENU");
	mm.addActionListener (this);
	mm.setActionCommand ("mm");
	//------------------------------

	Panel grid = new Panel (new GridLayout (20, 30)); //gridlayout for grid buttons

	for (int i = 0 ; i < mapButton.length ; i++) //initialize grid buttons
	{
	    mapButton [i] = new JButton (createImageIcon ("map_" + mapData [i / col] [i % col] + "_" + uAfil [i / col] [i % col] + "_" + uType [i / col] [i % col] + "_" + uState [i / col] [i % col] + ".gif")); //set appropriate icon
	    mapButton [i].setName ("" + i); //set name for mouse listener to distinguish buttons
	    mapButton [i].setPreferredSize (new Dimension (30, 30)); //resize to fit screen
	    mapButton [i].addActionListener (this);
	    mapButton [i].setActionCommand ("" + i); //set unique action command
	    mapButton [i].setBorderPainted (false); //no borders
	    mapButton [i].setOpaque (false); //buttons appear as only set icons
	    mapButton [i].setContentAreaFilled (false); //no button fill
	    grid.add (mapButton [i]); //add to button grid

	    mapButton [i].addMouseListener (mListener); //add mouse listener for all buttons
	}

	//add to respective panels
	time.add (timTimer);
	time.add (dTimerH);
	time.add (sm);
	time.add (dTimerM);
	time.add (sm2);
	time.add (dTimerS);

	left.add (lTitle);
	left.add (dIncome);
	left.add (dNum);
	left.add (dBuilding);

	iTerrain.add (tTitle);
	iTerrain.add (jPic);
	iTerrain.add (jType);
	iTerrain.add (jLoc);
	iTerrain.add (jAfil);
	iTerrain.add (jHealth);
	iTerrain.add (defTitle);
	iTerrain.add (jDef);

	iUnit.add (uTitle);
	iUnit.add (iPic);
	iUnit.add (iType);
	iUnit.add (iTeam);
	iUnit.add (advTitle);
	iUnit.add (iAdv);
	iUnit.add (disTitle);
	iUnit.add (iDis);
	iUnit.add (hTitle);
	iUnit.add (iHealth);
	iUnit.add (mTitle);
	iUnit.add (iMov);

	right.add (iTerrain);
	right.add (iUnit);

	bottom.add (endP);
	bottom.add (save);
	bottom.add (mm);

	p2.add (grid);

	p3.add (left, BorderLayout.WEST);
	p3.add (dTurn, BorderLayout.CENTER);
	p3.add (time, BorderLayout.EAST);

	//add to main panel
	p.add (p3, BorderLayout.NORTH);
	p.add (p2, BorderLayout.WEST);
	p.add (right, BorderLayout.EAST);
	p.add (bottom, BorderLayout.SOUTH);

	game.add ("3", p); //add main panel to 3th slot in game panel
    } //end addGS


    public void addWinScreen ()  //add win screen
    {
	//panel to place and sort widgets
	Panel p = new Panel ();

	//final time panel
	JPanel time = new JPanel ();
	time.setMaximumSize (new Dimension (200, 30));
	time.setBackground (new Color (221, 174, 107));

	//y-axis alignment for widgets in p
	BoxLayout bl = new BoxLayout (p, BoxLayout.Y_AXIS);
	p.setLayout (bl);

	JLabel title = new JLabel (createImageIcon ("results.png"));
	title.setAlignmentX (Component.CENTER_ALIGNMENT);

	//display winner, icon
	dWinner = new JLabel (createImageIcon ("win_r.png"));
	dWinner.setAlignmentX (Component.CENTER_ALIGNMENT);

	//display flag color of winner, icon
	dFlag = new JLabel (createImageIcon ("flag_r.png"));
	dFlag.setAlignmentX (Component.CENTER_ALIGNMENT);

	JLabel gRes = new JLabel ("GAME STATISTICS"); //Game Results title
	gRes.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 30));
	gRes.setAlignmentX (Component.CENTER_ALIGNMENT);

	JLabel timTimer = new JLabel ("TIMER");
	timTimer.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 25));
	timTimer.setAlignmentX (Component.CENTER_ALIGNMENT);

	dTimerH2 = new JLabel ("00"); //hour display
	dTimerH2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel sm = new JLabel (":");
	sm.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dTimerM2 = new JLabel ("00"); //hour display
	dTimerM2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel sm2 = new JLabel (":");
	sm2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	dTimerS2 = new JLabel ("00"); //hour display
	dTimerS2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel tTitle = new JLabel ("TURN COUNT");
	tTitle.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 25));
	tTitle.setAlignmentX (Component.CENTER_ALIGNMENT);

	dnTurn = new JLabel ("000"); //final number of turns display
	dnTurn.setFont (new Font ("MS Gothic", Font.PLAIN, 20));
	dnTurn.setAlignmentX (Component.CENTER_ALIGNMENT);

	JLabel cTitle = new JLabel ("CASUALTY COUNT");
	cTitle.setFont (new Font ("Rockwell Extra Bold", Font.BOLD, 25));
	cTitle.setAlignmentX (Component.CENTER_ALIGNMENT);

	dCasualties = new JLabel ("000"); //final number of casualties display
	dCasualties.setFont (new Font ("MS Gothic", Font.PLAIN, 20));
	dCasualties.setAlignmentX (Component.CENTER_ALIGNMENT);

	Panel space = new Panel (); //spacing to place mm2 at bottom of screen

	JButton mm2 = new JButton ("RETURN TO MAIN MENU");
	mm2.addActionListener (this);
	mm2.setActionCommand ("mm2");
	mm2.setAlignmentX (Component.CENTER_ALIGNMENT);

	//add widgets to respective panels
	time.add (dTimerH2);
	time.add (sm);
	time.add (dTimerM2);
	time.add (sm2);
	time.add (dTimerS2);

	//add widgets to main panel
	p.add (title);
	p.add (dWinner);
	p.add (dFlag);
	p.add (gRes);
	p.add (timTimer);
	p.add (time);
	p.add (tTitle);
	p.add (dnTurn);
	p.add (cTitle);
	p.add (dCasualties);
	p.add (space);
	p.add (mm2);

	game.add ("4", p); //add main panel to 4th slot in game panel
    } //end addWinScreen


    public void addFactory ()  //add widgets to factory pop-up frame
    {
	factory = new JFrame ("Factory"); //initialize frame

	//main panel
	Panel p = new Panel (new FlowLayout (5, 5, 5));
	p.setBackground (new Color (221, 174, 107));

	JLabel title = new JLabel ("Choose a unit! ");
	title.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iPrice2 = new JLabel ("Price: $     "); //init price, sizing widget
	iPrice2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	iPic2 = new JLabel (createImageIcon ("map_0_0_0_0.gif")); //unit animated .gif
	iPic2.setPreferredSize (new Dimension (30, 30));

	iType2 = new JLabel ("Type: -----------------"); //unit type, sizing widget
	iType2.setFont (new Font ("MS Gothic", Font.PLAIN, 20));

	JLabel advTitle = new JLabel ("Advantage: ");
	advTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iAdv2 = new JLabel (createImageIcon ("blank.gif")); //stand-in blank for size, changes to animated .gif
	iAdv2.setPreferredSize (new Dimension (30, 30));

	JLabel disTitle = new JLabel ("Disadvantage: ");
	disTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iDis2 = new JLabel (createImageIcon ("blank.gif")); //stand-in blank for size, changes to animated .gif
	iDis2.setPreferredSize (new Dimension (30, 30));

	JLabel mTitle = new JLabel ("Movement Range: ");
	mTitle.setFont (new Font ("MS Gothic", Font.BOLD, 20));

	iMov2 = new JLabel (createImageIcon ("stars_0.png")); //stand-in blank for sizing, changes to movement range of unit

	//drop-down selection to pick unit to spawn
	drop = new JComboBox (sel);
	drop.addActionListener (lFactory);
	drop.setActionCommand ("drop");
	drop.setSelectedIndex (0);

	//confirm selection
	confirm = new JButton ("(z) Confirm");
	confirm.addActionListener (lFactory);
	confirm.setActionCommand ("confirm");
	confirm.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'z' when window active
	    .put (KeyStroke.getKeyStroke ('z'), "pressed z");
	confirm.getActionMap ().put ("pressed z", key_z_fac); //do action key_z_fac when 'z' is pressed

	//cancel spawn unit
	JButton cancel = new JButton ("Cancel");
	cancel.addActionListener (lFactory);
	cancel.setActionCommand ("cancel");

	//add to main panel
	p.add (drop);
	p.add (iType2);
	p.add (iPic2);
	p.add (iPrice2);
	p.add (advTitle);
	p.add (iAdv2);
	p.add (disTitle);
	p.add (iDis2);
	p.add (mTitle);
	p.add (iMov2);
	p.add (confirm);
	p.add (cancel);

	//add main panel to Factory dialog
	factory.getContentPane ().add (title, BorderLayout.NORTH);
	factory.getContentPane ().add (p, BorderLayout.CENTER);

	//size dialog conforming around widgetss
	factory.pack ();
    } //end addFactory


    public void addAction ()  //add widgets to action menu pop-up frame
    {
	//initialize action menu pop-up dialog
	action = new JFrame ("Action");

	Panel p = new Panel (); //main panel
	p.setBackground (new Color (221, 174, 107));

	//button grid layout panel to organize uniformly-sized buttons
	Panel b = new Panel (new GridLayout (4, 1));

	//align widgets along y-axis
	BoxLayout bl = new BoxLayout (p, BoxLayout.Y_AXIS);
	p.setLayout (bl);

	JLabel title = new JLabel ("Action Menu");
	title.setFont (new Font ("Rockwell Extra Bold", Font.PLAIN, 15));

	//move unit button
	move = new JButton ("(a) Move");
	move.addActionListener (lAction);
	move.setActionCommand ("move");
	move.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'a' when window active
	    .put (KeyStroke.getKeyStroke ('a'), "pressed a");
	move.getActionMap ().put ("pressed a", key_a); //do action key_a when 'a' is pressed

	attack = new JButton ("(s) Attack");
	attack.addActionListener (lAction);
	attack.setActionCommand ("attack");
	attack.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 's' when window active
	    .put (KeyStroke.getKeyStroke ('s'), "pressed s");
	attack.getActionMap ().put ("pressed s", key_s); //do action key_s when 's' is pressed

	wait = new JButton ("(d) Wait");
	wait.addActionListener (lAction);
	wait.setActionCommand ("wait");
	wait.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'd' when window active
	    .put (KeyStroke.getKeyStroke ('d'), "pressed d");
	wait.getActionMap ().put ("pressed d", key_d); //do action key_d when 'd' is pressed

	cancel = new JButton ("(f) Cancel");
	cancel.addActionListener (lAction);
	cancel.setActionCommand ("cancel");
	cancel.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'f' when window active
	    .put (KeyStroke.getKeyStroke ('f'), "pressed f");
	cancel.getActionMap ().put ("pressed f", key_f); //do action key_f when 'f' is pressed

	//add buttons to button grid
	b.add (move);
	b.add (attack);
	b.add (wait);
	b.add (cancel);

	//add widgets to main panel
	p.add (title);
	p.add (b);

	//add main panel to action menu dialog
	action.getContentPane ().add (p, BorderLayout.CENTER);

	//size dialog conforming to widgets
	action.pack ();
    } //end addAction


    public void addBattleLog ()  //adds widgets to pop-up battle log dialog
    {
	//initialize battle log dialog
	bLog = new JFrame ("Battle Log");

	//main panel
	Panel p = new Panel ();
	p.setBackground (new Color (221, 174, 107));

	//y-axis alignment for widgets
	BoxLayout bl = new BoxLayout (p, BoxLayout.Y_AXIS);
	p.setLayout (bl);

	JLabel title = new JLabel ("Battle Log");
	title.setFont (new Font ("Rockwell Extra Bold", Font.PLAIN, 30));

	bLogTA = new JTextArea (""); //text area to display battle events
	bLogTA.setEditable (false); //uneditable
	bLogTA.setPreferredSize (new Dimension (500, 200)); //set initial size

	cont = new JButton ("(z) Continue");
	cont.addActionListener (this);
	cont.setActionCommand ("cont");
	cont.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'z' when window is active
	    .put (KeyStroke.getKeyStroke ('z'), "pressed z");
	cont.getActionMap ().put ("pressed z", key_z); //do action key_z when 'z' is pressed

	//add widgets to main panel
	p.add (title);
	p.add (bLogTA);
	p.add (cont);

	//add main panel to battle log dialog
	bLog.getContentPane ().add (p, BorderLayout.CENTER);

	bLog.pack (); //size dialog conforming to widgets
    } //end addBattleLog


    public void addTurnLog ()  //add widgets to Turn Log dialog
    {
	//initialize pop-up Turn Log
	tLog = new JFrame ();

	Panel p = new Panel (); //main panel
	p.setBackground (new Color (221, 174, 107));

	//y-axis alignment for widgets
	BoxLayout bl = new BoxLayout (p, BoxLayout.Y_AXIS);
	p.setLayout (bl);

	tTitle = new JLabel ("Begin" + ((turn == 0) ? " Red ":
	" Blue ") + "Turn");
	tTitle.setFont (new Font ("Rockwell Extra Bold", Font.PLAIN, 30));

	tLogTA = new JTextArea (""); //text area to dislay turn log events
	tLogTA.setEditable (false); //uneditable
	tLogTA.setPreferredSize (new Dimension (500, 200)); //set initial size

	cont2 = new JButton ("(z) Continue");
	cont2.addActionListener (this);
	cont2.setActionCommand ("cont");
	cont2.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW)  //listen to keypress 'z' when window is active
	    .put (KeyStroke.getKeyStroke ('z'), "pressed z");
	cont2.getActionMap ().put ("pressed z", key_z); //do action key_z when 'z' is pressed

	//add to main panel
	p.add (tTitle);
	p.add (tLogTA);
	p.add (cont2);

	//add to Turn Log Dialog
	tLog.getContentPane ().add (p, BorderLayout.CENTER);

	tLog.pack (); //size dialog conforming to widgets
    } //end addTurnLog


    public void startTimer ()  //initializes and starts game stopwatch
    {
	timer = new Timer (1000, tListener); //initialize timer object
	timer.start (); //start timer
    } //end startTimer


    public void initBuildings ()  //initialize building capture strengths
    {
	int maxbStrength[] = {0, 0, 0, 0, 10, 0, 10, 20, 20, 30, 30, 20, 20}; //set max capture strengths for buildings based upon mapData values

	for (int i = 0 ; i < mapButton.length ; i++) //assign max values to all terrain
	    if (bStrength [i / col] [i % col] != maxbStrength [mapData [i / col] [i % col]])
		bStrength [i / col] [i % col] = maxbStrength [mapData [i / col] [i % col]];
    } //end initBuildings


    public void updateBuildings ()  //update building strengths and types
    {
	int maxbStrength[] = {0, 0, 0, 0, 10, 0, 10, 20, 20, 10, 10, 20, 20}; //set max capture strengths for buildings based upon mapData values

	for (int i = 0 ; i < mapButton.length ; i++) //look through all grid squares
	{
	    //CAPTURE BUILDING CONDITIONS
	    //if there is an allied infantry unit on a enemy owned building
	    if (uType [i / col] [i % col] == 1 && uAfil [i / col] [i % col] == (turn + 1) && tAfil [mapData [i / col] [i % col]] != (turn + 1) && mapData [i / col] [i % col] > 3 && mapData [i / col] [i % col] != 5)
	    {
		//add line to turn log displaying what building is in the progress of being captured and its location
		tLogTA.append ("\n" + tName [mapData [i / col] [i % col]] + " at (" + ((i % col) + 1) + ", " + ((i / col) + 1) + ") is in progress of being captured! ");

		//substract building strength by health of unit on square
		bStrength [i / col] [i % col] -= uHealth [i / col] [i % col];

		//if unit has completely captured building, building strength is less than or equal to 0
		if (bStrength [i / col] [i % col] <= 0)
		{
		    //display that building at a certain location has been captured
		    tLogTA.append ("\n" + tName [mapData [i / col] [i % col]] + " at (" + ((i % col) + 1) + ", " + ((i / col) + 1) + ") has been captured");

		    if (mapData [i / col] [i % col] == 4) //if neutral city
		    {
			mapData [i / col] [i % col] = (turn == 0) ? 11: //set to red or blue city based upon current turn
			12;
		    }
		    else if (mapData [i / col] [i % col] == 6) //if neutral factory
		    {
			mapData [i / col] [i % col] = (turn == 0) ? 7: //set to red or blue factory based upon current turn
			8;
		    }
		    else if (mapData [i / col] [i % col] == 7 || mapData [i / col] [i % col] == 8) //if red or blue factory
		    {
			mapData [i / col] [i % col] = (turn == 0) ? 7: //set to red or blue factory depending on turn
			8;
			building [(turn == 0) ? 1: //subtract 1 from building count of opposing team
			0]--;
		    }
		    else if (mapData [i / col] [i % col] == 11 || mapData [i / col] [i % col] == 12) //if red or blue city
		    {
			mapData [i / col] [i % col] = (turn == 0) ? 11: //set to red or blue city depending on turn
			12;
			building [(turn == 0) ? 1: //subtract 1 from building count of opposing team
			0]--;
		    }
		    else if (mapData [i / col] [i % col] == 9 || mapData [i / col] [i % col] == 10) //if red or blue base
		    {
			mapData [i / col] [i % col] = (turn == 0) ? 9: //set to red or blue based depending on turn
			10;
			//no need to update building count, game ends
		    }
		    building [turn]++; //add 1 building to whoever's turn it is

		    tLogTA.append (" and has become a " + tName [mapData [i / col] [i % col]] + "! "); //display what building it has become and where
		}
	    }

	    //if no unit is on location and building strength is greater than 0 and building strength is less than max building strength
	    if (uAfil [i / col] [i % col] == 0 && bStrength [i / col] [i % col] > 0 && bStrength [i / col] [i % col] < maxbStrength [mapData [i / col] [i % col]])
	    {
		tLogTA.append ("\n" + ((turn == 0) ? "Blue":
		"Red") + " Infantry Unit has fled from " + tName [mapData [i / col] [i % col]] + " at (" + ((i % col) + 1) + ", " + ((i / col) + 1) + ")! ");
		bStrength [i / col] [i % col] = maxbStrength [mapData [i / col] [i % col]]; //reset building to max health
	    }
	    //else if building strength is less than 0 and is capturable building (building changes)
	    else if (bStrength [i / col] [i % col] <= 0 && tAfil [mapData [i / col] [i % col]] != 0)
		bStrength [i / col] [i % col] = maxbStrength [mapData [i / col] [i % col]]; //reset building to max health
	}
    }


    public void updateHealth ()  //heals units if they are standing on allied terrain
    {
	for (int i = 0 ; i < mapButton.length ; i++)
	{
	    //if unit health is < 10 and there is a unit on the terrain and terrain is builiding and units are allied
	    if (uHealth [i / col] [i % col] < 10 && uAfil [i / col] [i % col] != 0 && tAfil [mapData [i / col] [i % col]] != 0 && uAfil [i / col] [i % col] == turn + 1)
	    {
		//if red turn and red red building
		if ((turn == 0 && tAfil [mapData [i / col] [i % col]] == 1))
		{
		    tLogTA.append ("\nRed " + uName [uType [i / col] [i % col]] + " Unit recovers " + ((uHealth [i / col] [i % col] != 9) ? "2":
		    "1") + " health at " + ((i % col) + 1) + ", " + ((i / col) + 1) + "!"); //display that unit has recovered health
		    uHealth [i / col] [i % col] += 2; //add health
		}
		//if blue turn and blue building
		else if ((turn == 1 && tAfil [mapData [i / col] [i % col]] == 2))
		{
		    tLogTA.append ("\nBlue " + uName [uType [i / col] [i % col]] + " Unit recovers " + ((uHealth [i / col] [i % col] != 9) ? "2":
		    "1") + " health at " + ((i % col) + 1) + ", " + ((i / col) + 1) + "!"); //display that unit has recovered health
		    uHealth [i / col] [i % col] += 2; //add health
		}

		if (uHealth [i / col] [i % col] > 10) //if health is greater than max, set to max
		    uHealth [i / col] [i % col] = 10;
	    }
	}
    }


    public void initMoveGrid (int unit)  //set beginning and obstacles for pathfinding
    {
	int range = (uMov [unit] + 1)*2; //get movement range

	if (unit == 3 || unit == 4) //if unit is vehicle
	{
	    for (int i = 0 ; i < mapButton.length ; i++) //make water an obstacle
		if (mapData [i / col] [i % col] == 5)
		    movGrid [i / col] [i % col] = -3;
	}
	for (int i = 0 ; i < mapButton.length ; i++) //mark allied units, passable
	    if (uAfil [i / col] [i % col] == (turn + 1))
		movGrid [i / col] [i % col] = -1;
	    else if (uAfil [i / col] [i % col] != 0) //mark enemy unit, obstacles
		movGrid [i / col] [i % col] = -2;

	movGrid [loc / col] [loc % col] = range; //set starting location

	moveScan (range); //scan for valid locations
    }


    public void moveScan (int range)  //using Dijkstra's pathfinding, designate and highlight vaiable movement locations
    {
	for (int i = 0 ; i < range; i++) //while number sets are less than movement range
	{
	    for (int j = 0 ; j < mapButton.length ; j++) //search to entire grid
	    {
		if (movGrid [j / col] [j % col] == range - i) //if marked as viable, set adjacent squares as viable movement locations as well while restricting movement range depending on terrain defense (also, blocks array from going out of bounds)
		{
		    if (j / col > 0 && (movGrid [(j / col) - 1] [(j % col)] == 0 || movGrid [(j / col) - 1] [(j % col)] == -1))
			movGrid [(j / col) - 1] [(j % col)] = range - i - 1 - (tDef [mapData [j / col] [j % col]] - 1);
		    if ((j / col) < (row - 1) && (movGrid [(j / col) + 1] [(j % col)] == 0 || movGrid [(j / col) + 1] [(j % col)] == -1))
			movGrid [(j / col) + 1] [(j % col)] = range - i - 1 - (tDef [mapData [j / col] [j % col]] - 1);
		    if ((j % col > 0) && (movGrid [(j / col)] [(j % col) - 1] == 0 || movGrid [(j / col)] [(j % col) - 1] == -1))
			movGrid [(j / col)] [(j % col) - 1] = range - i - 1 - (tDef [mapData [j / col] [j % col]] - 1);
		    if (((j % col) < (col - 1)) && (movGrid [(j / col)] [(j % col) + 1] == 0 || movGrid [(j / col)] [(j % col) + 1] == -1))
			movGrid [(j / col)] [(j % col) + 1] = range - i - 1 - (tDef [mapData [j / col] [j % col]] - 1);
		}
	    }
	}
	for (int i = 0 ; i < mapButton.length ; i++) //search, and for any marked locations, set it to movement active
	    if (movGrid [i / col] [i % col] > 0)
		moveAct (mapButton [i].getName ());
    }


    public void moveAct (String a)  //activate given square by taking name
    {
	int i = Integer.parseInt (a); //parse location name into integer
	if (uAfil [i / col] [i % col] == 0) //do not change starting location to viable movement location
	{
	    mapButton [i].setIcon (createImageIcon ("map_" + mapData [i / col] [i % col] + "_0_0_0_b.gif")); //blue overlay
	    mapButton [i].setActionCommand ("" + i); //activate button with actionCommand
	}
    } //end moveAct


    public void moveUnit (int i)  //relocate the unit and all of its arra values
    {
	uAfil [loc / col] [loc % col] = uAfil [movLoc / col] [movLoc % col]; //move afiliation value
	uType [loc / col] [loc % col] = uType [movLoc / col] [movLoc % col]; //move unit type value
	uState [loc / col] [loc % col] = 1; //move unit state value
	uHealth [loc / col] [loc % col] = uHealth [movLoc / col] [movLoc % col]; //move unit health value

	//set all previous location values to 0
	uAfil [movLoc / col] [movLoc % col] = 0;
	uType [movLoc / col] [movLoc % col] = 0;
	uState [movLoc / col] [movLoc % col] = 0;
	uHealth [movLoc / col] [movLoc % col] = 0;

	moving = false; //not moving
	moved = true; //set has moved true
	move.setEnabled (false); //disable move button so unit cannot move again
	resetMoveGrid (); //delete pathfinding values in move grid

	if ((uType [loc / col] [loc % col] == 4 && pmAttackScan () == true) || pAttackScan () == true) //if can attack
	    attack.setEnabled (true); //enable attack button

	wait.setEnabled (true); //enable wait button
	action.requestFocus (); //give focus to action menu dialog
	redraw (); //redraw screen
    } //end moveUnit


    public void resetMoveGrid ()  //reset move grid pathfinding values
    {
	for (int i = 0 ; i < mapButton.length ; i++)
	    movGrid [i / col] [i % col] = 0;
    } //end resetMoveGrid


    public void redraw ()  //redraw screen
    {
	for (int i = 0 ; i < mapButton.length ; i++) //reset all grid displays to changed values
	{
	    if (uState [i / col] [i % col] == 0 && uAfil [i / col] [i % col] != 0)
		mapButton [i].setIcon (createImageIcon ("map_" + mapData [i / col] [i % col] + "_1_" + uType [i / col] [i % col] + "_" + uState [i / col] [i % col] + ".gif"));
	    else
		mapButton [i].setIcon (createImageIcon ("map_" + mapData [i / col] [i % col] + "_" + uAfil [i / col] [i % col] + "_" + uType [i / col] [i % col] + "_" + uState [i / col] [i % col] + ".gif"));
	}
    } //end redraw


    public void resetAction ()  //revert unit location
    {
	//move all unit values back to initial location
	uAfil [movLoc / col] [movLoc % col] = uAfil [loc / col] [loc % col];
	uType [movLoc / col] [movLoc % col] = uType [loc / col] [loc % col];
	uState [movLoc / col] [movLoc % col] = 1;
	uHealth [movLoc / col] [movLoc % col] = uHealth [loc / col] [loc % col];

	//reset unit values for new location
	uAfil [loc / col] [loc % col] = 0;
	uType [loc / col] [loc % col] = 0;
	uState [loc / col] [loc % col] = 0;
	uHealth [loc / col] [loc % col] = 0;

	moving = false; //is not moving
	moved = false; //has not moved
	resetMoveGrid (); //reset move grid and overlay

	//swap new and old locations
	int temp = movLoc;
	loc = movLoc;
	movLoc = temp;
    } //end resetAction


    public boolean pAttackScan ()  //for standard units (not missile), look to see if attack is valid
    {
	boolean valid = false;
	if (turn == 0) //if red turn
	{
	    //if enemy unit in adjacent square, set valid to true
	    if ((loc / col > 0) && uAfil [(loc / col) - 1] [loc % col] == 2)
		valid = true;
	    else if ((loc / col) < (row - 1) && uAfil [(loc / col) + 1] [loc % col] == 2)
		valid = true;
	    else if ((loc % col > 0) && uAfil [loc / col] [(loc - 1) % col] == 2)
		valid = true;
	    else if ((loc % col) < (col - 1) && uAfil [loc / col] [(loc + 1) % col] == 2)
		valid = true;
	}
	else //if blue turn
	{
	    //if enemy unit in adjacent square, set valid to true
	    if ((loc / col > 0) && uAfil [(loc / col) - 1] [loc % col] == 1)
		valid = true;
	    else if ((loc / col) < (row - 1) && uAfil [(loc / col) + 1] [loc % col] == 1)
		valid = true;
	    else if ((loc % col > 0) && uAfil [loc / col] [(loc - 1) % col] == 1)
		valid = true;
	    else if ((loc % col) < (col - 1) && uAfil [loc / col] [(loc + 1) % col] == 1)
		valid = true;
	}
	//return whether attack command is valid or not
	return valid;
    } //end pAttackScan


    public void attackScan ()  //endable buttons that are valid
    {
	if (turn == 0) //if red turn
	{
	    //if enemy unit in adjacent square, activate button of enemy so that it is selectable to attack
	    if ((loc / col > 0) && uAfil [(loc / col) - 1] [loc % col] == 2)
		mapButton [loc - col].setActionCommand ("" + (loc - col));
	    else if ((loc / col) < (col) && uAfil [(loc / col) + 1] [loc % col] == 2)
		mapButton [loc + col].setActionCommand ("" + (loc + col));
	    else if ((loc % col > 0) && uAfil [loc / col] [(loc - 1) % col] == 2)
		mapButton [loc - 1].setActionCommand ("" + (loc - 1));
	    else if ((loc % col) < (col) && uAfil [loc / col] [(loc + 1) % col] == 2)
		mapButton [loc + 1].setActionCommand ("" + (loc + 1));
	}
	else //if blue turn
	{
	    //if enemy unit in adjacent square, activate button of enemy so that it is selectable to attack
	    if ((loc / col > 0) && uAfil [(loc / col) - 1] [loc % col] == 1)
		mapButton [loc - col].setActionCommand ("" + (loc - col));
	    else if ((loc / col) < (col - 1) && uAfil [(loc / col) + 1] [loc % col] == 1)
		mapButton [loc + col].setActionCommand ("" + (loc + col));
	    else if ((loc % col > 0) && uAfil [loc / col] [(loc - 1) % col] == 1)
		mapButton [loc - 1].setActionCommand ("" + (loc - 1));
	    else if ((loc % col) < (col) && uAfil [loc / col] [(loc + 1) % col] == 1)
		mapButton [loc + 1].setActionCommand ("" + (loc + 1));
	}
    } //end attackScan


    public boolean pmAttackScan ()  //attack scan for missile unit and returns if attack is valid
    {
	//if there is an enemy unit on grid, attack is valid
	int count = 0;
	for (int i = 0 ; i < mapButton.length ; i++)
	{
	    if (turn == 0)
		if (uAfil [i / col] [i % col] == 2)
		    count++;
	    if (turn == 1)
		if (uAfil [i / col] [i % col] == 1)
		    count++;
	}
	if (count > 0)
	    return true;
	else
	    return false;
    } //end pmAttackScan


    public void mAttackScan ()  //attack scan for missile unit, activates buttons
    {
	//if unit on grid square, activate button such that it is selectable for attack
	for (int i = 0 ; i < mapButton.length ; i++)
	{
	    if (turn == 0)
		if (uAfil [i / col] [i % col] == 2)
		    mapButton [i].setActionCommand ("" + i);
	    if (turn == 1)
		if (uAfil [i / col] [i % col] == 1)
		    mapButton [i].setActionCommand ("" + i);
	}
    } //end mAttackScan


    public void attackUnit (int tLoc)  //gets target location and assigns appropriate health values to both units based on damage calculations, updates Battle Log
    {
	//assign relevant array values of units to short-name local variables for simplicity
	int tUnit = uType [tLoc / col] [tLoc % col];
	int aUnit = uType [loc / col] [loc % col];
	int tHealth = uHealth [tLoc / col] [tLoc % col];
	int aHealth = uHealth [loc / col] [loc % col];

	tHealth -= damageCalc (false, loc, tLoc); //calculate damage of target
	if ((aUnit == 4 && tUnit == 4) || aUnit != 4)
	    aHealth -= damageCalc (true, tLoc, loc); //calculate damage of attacker if missile unit is not attacking standard unit

	bLogUpdate (tLoc, uHealth [tLoc / col] [tLoc % col] - tHealth, uHealth [loc / col] [loc % col] - aHealth, aHealth, tHealth); //update battle log text, insert health and damage of both units

	//change array values to modified local values
	uHealth [tLoc / col] [tLoc % col] = tHealth;
	uHealth [loc / col] [loc % col] = aHealth;

	attacking = false; //not attacking

	bLog.setVisible (true); //activate battle log dialog

	//if unit health is 0, delete unit
	if (aHealth <= 0) //attacker
	    deleteUnit (loc);
	else if (tHealth <= 0) //defender
	    deleteUnit (tLoc);

	moved = false; //has not moved

	move.setEnabled (true); //enable move button for next unit

	//change attacker icon to inactive unit
	mapButton [loc].setIcon (createImageIcon ("map_" + mapData [loc / col] [loc % col] + "_1_" + uType [loc / col] [loc % col] + "_0.gif")); //clear dead unit icon

	bLog.requestFocus (); //set focus
    } //end attackUnit


    public int damageCalc (boolean attacker, int loc, int tLoc)  //take who attacked, location of attacker, and location of target and determine damage dealt to a unit
    {
	//set unit types to local variables
	int aType = uType [loc / col] [loc % col];
	int tType = uType [tLoc / col] [tLoc % col];

	int damage = uHealth [loc / col] [loc % col]; //damage = unit health
	damage /= 2; //divide damage by 2
	damage -= tDef [mapData [tLoc / col] [tLoc % col]]; //subtract damage by terrain defense of target

	if (aType == uDis [tType]) //if advantage, add 2 to damage
	    damage += 2;
	if (attacker == false) //if attacker, add 1 to damage
	    damage += 1;

	int chance = ((int) (Math.random ()) * 3); //add a random value to damage
	damage += chance;

	if (damage > 0) //if damage, return damage
	    return damage;
	else //if no damage or negative damage, return 0 (no damage)
	    return 0;
    } //end damageCalc


    public void deleteUnit (int loc)  //delete unit
    {
	casualties++; //add 1 to total number of game casualties

	uNum [uAfil [loc / col] [loc % col] - 1]--;
	dNum.setText ("Unit Count: " + (uAfil [loc / col] [loc % col] - 1));

	//reset values of dead unit
	uAfil [loc / col] [loc % col] = 0;
	uType [loc / col] [loc % col] = 0;
	uHealth [loc / col] [loc % col] = 0;
	uState [loc / col] [loc % col] = 0;

	mapButton [loc].setIcon (createImageIcon ("map_" + mapData [loc / col] [loc % col] + "_0_0_0.gif")); //clear dead unit icon
    } //end deleteUnit


    public void endPhase ()  //when phase ends, do certain procedures
    {
	disableButtons (); //disable all buttons

	nTurn++; //add 1 to total turn count

	//stop music and start music corresponding to whose turn it is
	stopMusic ();

	if (turn == 0)
	{
	    turn = 1;
	    playMusic ("step3");
	}
	else
	{
	    turn = 0;
	    playMusic ("step1");
	}

	dTurn.setIcon (createImageIcon ("turn_" + turn + ".png")); //change turn display to whose turn it is

	//set state of all units to active
	for (int i = 0 ; i < mapButton.length ; i++)
	    if (uType [i / col] [i % col] != 0)
	    {
		uState [i / col] [i % col] = 1;
		//change icon
		mapButton [i].setIcon (createImageIcon ("map_" + mapData [i / col] [i % col] + "_" + uAfil [i / col] [i % col] + "_" + uType [i / col] [i % col] + "_1.gif"));
	    }

	//Turn Log Title, correspond with whose turn it is
	tTitle.setText ("Begin" + ((turn == 0) ? " Red ":
	" Blue ") + "Turn");

	tLogUpdate (); //update turn log

	//update team info displays to correspond to whose turn it is
	income [turn] += 100 * building [turn];
	dIncome.setText ("Income: $" + income [turn]);
	dNum.setText ("Unit Count: " + uNum [turn]);
	dBuilding.setText ("Building Count: " + building [turn]);

	//set default factory choice to infantry and display corresponding unit pictures
	drop.setSelectedIndex (0);
	iPic2.setIcon (createImageIcon ("unit_" + ((turn == 0) ? "1":
	"2") + "_1.gif"));
	iType2.setText ("Type: Infantry");
	iAdv2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
	"1")) + "_2.gif")));
	iDis2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
	"1")) + "_3.gif")));
	iMov2.setIcon (createImageIcon ("stars_4.png"));

	redraw (); //redraw screen

	//enable Turn Log
	tLog.setVisible (true);
	tLog.requestFocus ();

	if (checkWin () != 'c') //check for win, if win, go to win screen
	    winScreen (checkWin ());
    } //end endPhase


    public void winScreen (char winner)  //show win screen with winner and other information
    {
	timer.stop ();

	dTimerS.setText (((second < 10) ? "0":
	"") + second);
	dTimerM.setText (((minute < 10) ? "0":
	"") + minute);
	dTimerH.setText (((hour < 10) ? "0":
	"") + hour);

	dWinner.setIcon (createImageIcon ("win_" + winner + ".png"));
	dFlag.setIcon (createImageIcon ("flag_" + winner + ".png"));
	dCasualties.setText ("" + casualties);
	dnTurn.setText ("" + nTurn);

	card.show (game, "4");
    } //end winScreen


    public void randTurn ()  //randomize turn (called at beginning of game)
    {
	int n = (int) ((Math.random () * 2));
	turn = n;
    } //end randTurn


    public boolean checkPrice (int unit)  //check price of unit and if factory decision is valid
    {
	if (income [turn] >= uPrice [unit])
	    return true;
	else
	    return false;
    } //end checkPrice


    public void addUnit (int pos, int choice)  //add unit to screen
    {
	//change unit icon
	mapButton [pos].setIcon (createImageIcon ("map_" + mapData [pos / col] [pos % col] + "_1_" + choice + "_0.gif"));

	//add unit values to arrays
	uAfil [pos / col] [pos % col] = (turn + 1);
	uType [pos / col] [pos % col] = choice;
	uState [pos / col] [pos % col] = 0;
	uHealth [pos / col] [pos % col] = 10;

	//subtract price from income of whose turn it is
	income [turn] -= uPrice [choice];
	dIncome.setText ("Income: $" + income [turn]);

	//add 1 to unit number of whose turn it is
	uNum [turn]++;
	dNum.setText ("Unit Count: " + uNum [turn]);
    } //end addUnit


    public void bLogUpdate (int tLoc, int tDamage, int aDamage, int aHealth, int tHealth)  //update battle log with appropriate text
    {
	String teamName[] = {"", "Red", "Blue"};

	//who attacked who
	String a = (turn == 0) ? "Red ":
	"Blue ";
	a += uName [uType [loc / col] [loc % col]] + " Unit attacks ";
	a += (turn == 0) ? "Blue ":
	"Red ";
	a += uName [uType [tLoc / col] [tLoc % col]] + " Unit!";

	//attacker location
	String b = "\n";
	b += (turn == 0) ? "Red ":
	"Blue ";
	b += uName [uType [loc / col] [loc % col]] + " Unit takes cover in " + tName [mapData [loc / col] [loc % col]] + "!";

	//defender location
	String c = "\n";
	c += (turn == 0) ? "Blue ":
	"Red ";
	c += uName [uType [loc / col] [loc % col]] + " Unit takes cover in " + tName [mapData [tLoc / col] [tLoc % col]] + "!";

	//type advantage
	String d = "\n";
	if (uType [loc / col] [loc % col] == uType [tLoc / col] [tLoc % col])
	    d += "Neither units have a type advantage! ";
	else if (uType [loc / col] [loc % col] == 4)
	{
	    d += "Attacking ";
	    d += (turn == 0) ? "Red ":
	    "Blue ";
	    d += "Missile Unit is untouchable at a range!";
	}
	else
	{
	    d += "Attacking ";
	    d += (turn == 0) ? "Red ":
	    "Blue ";
	    d += uName [uType [loc / col] [loc % col]] + " has the ";
	    d += (uAdv [uType [loc / col] [loc % col]] == uType [tLoc / col] [tLoc % col]) ? "advantage ":
	    "disadvantage ";
	    d += "against the defending ";
	    d += (turn == 0) ? "Blue ":
	    "Red ";
	    d += uName [uType [tLoc / col] [tLoc % col]] + " Unit!";
	}

	//attacker damage
	String e = "\n";
	e += (turn == 0) ? "Red ":
	"Blue ";
	e += uName [uType [loc / col] [loc % col]] + " takes " + aDamage + " damage!";

	//defender damage
	String f = "\n";
	f += (turn == 0) ? "Blue ":
	"Red ";
	f += uName [uType [tLoc / col] [tLoc % col]] + " takes " + tDamage + " damage!";

	//death of unit
	String g = "\n\n";
	if (aHealth <= 0 && tHealth <= 0)
	    g += "Both units died! ";
	else if (aHealth > 0 && tHealth > 0)
	    g += "Both units survived! ";
	else
	{
	    g += (aHealth <= 0) ? "Attacking ":
	    "Defending ";
	    g += (aHealth <= 0) ? teamName [uAfil [loc / col] [loc % col]]:
	    teamName [uAfil [tLoc / col] [tLoc % col]];
	    g += " ";
	    g += (aHealth <= 0) ? uName [uType [loc / col] [loc % col]]:
	    uName [uType [tLoc / col] [tLoc % col]];
	    g += " Unit eliminated! ";
	}

	//add text to battle log
	bLogTA.append (a);
	bLogTA.append (b);
	bLogTA.append (c);
	bLogTA.append (d);
	bLogTA.append (e);
	bLogTA.append (f);
	bLogTA.append (g);
    } //end bLogUpdate


    public void tLogUpdate ()  //updates turn log with appropriate text
    {
	String teamName[] = {"", "Red", "Blue"};

	//income from earned buildings
	tLogTA.append (teamName [turn + 1] + " has earned $" + (building [turn] * 100) + " for owning " + building [turn] + " buildings! ");
	updateBuildings (); //building updates + text
	updateHealth (); //health updates + text
    } //end tLogUpdate


    public char checkWin ()  //check if game has been won
    {
	int n = 0;
	//check for red bases
	for (int i = 0 ; i < mapButton.length ; i++)
	    if (mapData [i / col] [i % col] == 9)
		n++;
	switch (n)
	{
	    case 0: //if none, blue wins
		return 'b';

	    case 1: //if 1, game continues
		return 'c';

	    case 2: //if 2, red wins
		return 'r';

	    default: //error
		return '?';
	}
    } //end checkWin


    public void disableButtons ()  //disable grid buttons (sets action command to nothing) + bottom buttons on game screen
    {
	for (int i = 0 ; i < mapButton.length ; i++)
	    mapButton [i].setActionCommand ("");
	endP.setEnabled (false);
	save.setEnabled (false);
	mm.setEnabled (false);
    } //end disableButtons


    public void disableMenu ()  //disable action menu buttons except for cancel
    {
	move.setEnabled (false);
	attack.setEnabled (false);
	wait.setEnabled (false);
    } //end disableButtons


    public void enableButtons ()  //enable grid buttons + bottom buttons on game screen
    {
	for (int i = 0 ; i < mapButton.length ; i++)
	    mapButton [i].setActionCommand ("" + i);
	endP.setEnabled (true);
	save.setEnabled (true);
	mm.setEnabled (true);
    } //end enableButtons


    public void resetGame ()  //reset all arrays to initial values
    {
	//terrain values
	mapData = new int[] []
	{
	    {
		4, 3, 0, 0, 0, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 0, 3, 4
	    }
	    ,
	    {
		3, 0, 0, 4, 0, 2, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 0, 4, 0, 0, 3
	    }
	    ,
	    {
		0, 0, 0, 1, 0, 2, 5, 0, 1, 0, 0, 1, 1, 0, 4, 1, 1, 0, 0, 1, 1, 0, 0, 5, 2, 0, 1, 0, 0, 0
	    }
	    ,
	    {
		0, 0, 4, 0, 0, 2, 5, 1, 1, 0, 0, 1, 1, 0, 3, 4, 1, 0, 0, 1, 1, 0, 0, 5, 2, 0, 0, 4, 0, 0
	    }
	    ,
	    {
		4, 0, 1, 0, 0, 2, 5, 0, 0, 1, 1, 0, 0, 1, 4, 3, 0, 1, 1, 0, 0, 1, 1, 5, 2, 0, 0, 1, 0, 4
	    }
	    ,
	    {
		0, 0, 0, 0, 3, 2, 5, 0, 0, 1, 1, 0, 0, 1, 3, 4, 0, 1, 1, 0, 0, 1, 1, 5, 2, 3, 0, 0, 0, 0
	    }
	    ,
	    {
		3, 0, 0, 3, 4, 2, 5, 1, 1, 0, 0, 1, 1, 0, 4, 3, 1, 0, 0, 1, 1, 0, 0, 5, 2, 4, 3, 0, 0, 3
	    }
	    ,
	    {
		4, 3, 3, 1, 1, 2, 5, 1, 1, 0, 0, 1, 1, 0, 3, 4, 1, 0, 0, 1, 1, 0, 0, 5, 2, 1, 1, 3, 3, 4
	    }
	    ,
	    {
		7, 1, 7, 1, 1, 2, 5, 0, 0, 1, 1, 0, 0, 1, 6, 3, 0, 1, 1, 0, 0, 1, 1, 5, 2, 1, 1, 8, 1, 8
	    }
	    ,
	    {
		2, 9, 2, 2, 2, 4, 2, 0, 0, 1, 1, 0, 0, 1, 3, 6, 0, 1, 1, 0, 0, 1, 1, 2, 4, 2, 2, 2, 10, 2
	    }
	    ,
	    {
		7, 1, 7, 4, 1, 2, 5, 1, 1, 0, 0, 1, 1, 0, 6, 3, 1, 0, 0, 1, 1, 0, 0, 5, 2, 1, 4, 8, 1, 8
	    }
	    ,
	    {
		4, 3, 1, 1, 1, 2, 5, 1, 1, 0, 0, 1, 1, 0, 3, 6, 1, 0, 0, 1, 1, 0, 0, 5, 2, 1, 1, 1, 3, 4
	    }
	    ,
	    {
		3, 0, 3, 1, 1, 2, 5, 0, 0, 1, 1, 0, 0, 1, 4, 3, 0, 1, 1, 0, 0, 1, 1, 5, 2, 1, 1, 3, 0, 3
	    }
	    ,
	    {
		0, 0, 0, 3, 4, 2, 5, 0, 0, 1, 1, 0, 0, 1, 3, 4, 0, 1, 1, 0, 0, 1, 1, 5, 2, 4, 3, 0, 0, 0
	    }
	    ,
	    {
		0, 0, 0, 0, 3, 2, 5, 1, 1, 0, 0, 1, 1, 0, 4, 3, 1, 0, 0, 1, 1, 0, 0, 5, 2, 3, 0, 0, 0, 0
	    }
	    ,
	    {
		4, 0, 1, 0, 0, 2, 5, 1, 1, 0, 0, 1, 1, 0, 3, 4, 1, 0, 0, 1, 1, 0, 0, 5, 2, 0, 0, 1, 0, 4
	    }
	    ,
	    {
		0, 0, 4, 0, 0, 2, 5, 0, 0, 1, 1, 0, 0, 1, 4, 3, 0, 1, 1, 0, 0, 1, 1, 5, 2, 0, 0, 4, 0, 0
	    }
	    ,
	    {
		0, 0, 0, 1, 0, 2, 5, 0, 0, 1, 1, 0, 0, 1, 1, 4, 0, 1, 1, 0, 0, 1, 1, 5, 2, 0, 1, 0, 0, 0
	    }
	    ,
	    {
		3, 0, 0, 4, 0, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 5, 5, 5, 5, 5, 5, 5, 5, 2, 0, 4, 0, 0, 3
	    }
	    ,
	    {
		4, 3, 0, 0, 0, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0, 0, 3, 4
	    }
	}
	;
	for (int i = 0 ; i < row ; i++)
	    for (int j = 0 ; j < col ; j++)
	    {
		//no units on screen
		uAfil [i] [j] = 0;
		uType [i] [j] = 0;
		uHealth [i] [j] = 0;
		uState [i] [j] = 0;
	    }
	for (int i = 0 ; i < 2 ; i++)
	{
	    //income, unit number, building count reset
	    income [i] = 0;
	    uNum [i] = 0;
	    building [i] = 5;
	}

    }


    public void initGame ()  //initialize game procedures
    {
	resetGame ();
	randTurn ();
	endPhase ();
	startTimer ();
	card.show (game, "3");
    } //end initGame


    public void quit ()  //close game
    {
	int select = JOptionPane.showConfirmDialog (null, "Are you sure?", "QUIT GAME", JOptionPane.YES_NO_OPTION);
	if (select == JOptionPane.YES_OPTION)
	    System.exit (0);
    } //quit game


    public void saveGame ()  //save array values to save.txt to be loaded at another time
    {
	try //if save.txt exists
	{
	    //print to save.txt
	    PrintWriter out = new PrintWriter (new FileWriter ("save.txt"));
	    //reset save file
	    out.print ("");

	    //add value, and then add space
	    for (int a = 0 ; a < mapButton.length ; a++)
		out.write (mapData [a / col] [a % col] + " ");
	    for (int b = 0 ; b < mapButton.length ; b++)
		out.write (uAfil [b / col] [b % col] + " ");
	    for (int c = 0 ; c < mapButton.length ; c++)
		out.write (uType [c / col] [c % col] + " ");
	    for (int d = 0 ; d < mapButton.length ; d++)
		out.write (uHealth [d / col] [d % col] + " ");
	    for (int e = 0 ; e < mapButton.length ; e++)
		out.write (uState [e / col] [e % col] + " ");
	    for (int f = 0 ; f < mapButton.length ; f++)
		out.write (bStrength [f / col] [f % col] + " ");

	    out.write (turn + " ");
	    out.write (nTurn + " ");
	    out.write (casualties + " ");
	    out.write (hour + " ");
	    out.write (minute + " ");
	    out.write (second + " ");
	    out.write (income [0] + " ");
	    out.write (income [1] + " ");
	    out.write (uNum [0] + " ");
	    out.write (uNum [1] + " ");
	    out.write (building [0] + " ");
	    out.write (building [1] + " ");

	    out.close ();

	    JOptionPane.showMessageDialog (null, "Game Saved! Choose \"LOAD GAME\" from the main menu to continue! ");
	}
	catch (IOException e)  //if save.txt does not exist, create new and save
	{
	    File file = new File ("save.txt");
	    saveGame ();
	}
    } //end saveGame


    public void loadGame ()  //loads game from values in save.txt
    {
	try //if save.txt exists
	{
	    int i = 0;

	    //import save.txt text
	    BufferedReader in = new BufferedReader (new FileReader ("save.txt"));

	    String input = in.readLine ();

	    String value = "";

	    try //do if arrays do not go out of bounds
	    {
		//GENERAL PROCEDURE
		//iterate through all values in array, iterate through all characters in save.txt and if there is a space, assign value to corresponding array value
		for (int a = 0 ; a < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {
			mapData [a / col] [a % col] = Integer.parseInt (value);
			value = "";
			a++;
		    }

		    else
			value += input.charAt (i);

		}
		for (int b = 0 ; b < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {

			uAfil [b / col] [b % col] = Integer.parseInt (value);
			value = "";
			b++;
		    }
		    else
			value += input.charAt (i);
		}
		for (int c = 0 ; c < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {
			uType [c / col] [c % col] = Integer.parseInt (value);
			value = "";
			c++;
		    }
		    else
			value += input.charAt (i);
		}
		for (int d = 0 ; d < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {
			uHealth [d / col] [d % col] = Integer.parseInt (value);
			value = "";
			d++;
		    }
		    else
			value += input.charAt (i);
		}
		for (int e = 0 ; e < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {
			uState [e / col] [e % col] = Integer.parseInt (value);
			value = "";
			e++;
		    }
		    else
			value += input.charAt (i);
		}
		for (int f = 0 ; f < mapButton.length ; i++)
		{
		    if (input.charAt (i) == ' ')
		    {
			bStrength [f / col] [f % col] = Integer.parseInt (value);
			value = "";
			f++;
		    }
		    else
			value += input.charAt (i);
		}

		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		turn = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		nTurn = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		casualties = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		hour = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		minute = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		second = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		income [0] = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		income [1] = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		uNum [0] = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		uNum [1] = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		building [0] = Integer.parseInt (value);
		i++;
		value = "";
		for (; input.charAt (i) != ' ' ; i++)
		    value += input.charAt (i);
		building [1] = Integer.parseInt (value);
		i++;
		value = "";

		in.close ();

		//set appropriate screen displays to display correct info
		dTurn.setIcon (createImageIcon ("turn_" + turn + ".png"));
		dIncome.setText ("Income: $" + income [turn]);
		dNum.setText ("Unit Count: " + uNum [turn]);
		dBuilding.setText ("Building Count: " + building [turn]);
		dTimerS.setText (((second < 10) ? "0":
		"") + second);
		dTimerM.setText (((minute < 10) ? "0":
		"") + minute);
		dTimerH.setText (((hour < 10) ? "0":
		"") + hour);
		drop.setSelectedIndex (0);
		iPic2.setIcon (createImageIcon ("unit_" + ((turn == 0) ? "1":
		"2") + "_1.gif"));
		iType2.setText ("Type: Infantry");
		iAdv2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
		"1")) + "_2.gif")));
		iDis2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
		"1")) + "_3.gif")));
		iMov2.setIcon (createImageIcon ("stars_4.png"));

		stopMusic ();
		if (turn == 0)
		    playMusic ("step1");
		else
		    playMusic ("step3");

		jType.setText ("Type: -----------------");
		jLoc.setText ("---------------------------");
		iHealth.setIcon (createImageIcon ("health_1.png"));

		startTimer ();
		redraw ();

		endP.requestFocus ();

		card.show (game, "3");
	    }
	    catch (NullPointerException e)  //show error dialog if save file is invalid or not created
	    {
		JOptionPane.showMessageDialog (null, "Invalid save file! Please create a new one! ");
	    }
	}
	catch (IOException e)  //show error dialog if save.txt does not exist
	{
	    JOptionPane.showMessageDialog (null, "Cannot find save.txt! Start new game with \"TWO PLAYER\" button! ");
	}
    } //end loadGame


    public void actionPerformed (ActionEvent e)
    {
	if (e.getActionCommand ().equals ("")) //if no action command, do nothing
	{
	}
	else if (e.getActionCommand ().equals ("play")) //go to and initialize intro
	{
	    nScript = 0;
	    dIntro.setText ("\n\n\n\n\n\n\n\n\n\n\n\n");
	    card.show (game, "2");
	    dIntro.setText (intro [nScript]);
	}
	else if (e.getActionCommand ().equals ("load")) //load game from save.txt data and display screen
	    loadGame ();
	else if (e.getActionCommand ().equals ("quit")) //quit game
	    quit ();
	else if (e.getActionCommand ().equals ("next")) //next line in intro script
	{
	    nScript++;
	    if (nScript == intro.length) //at end of intro script, ask if user(s) want to view tutorial. Also, prevents out-of-bounds
	    {
		int select = JOptionPane.showConfirmDialog (null, "Would you like to view the tutorial?", "TUTORIAL", JOptionPane.YES_NO_OPTION);
		if (select == JOptionPane.YES_OPTION)
		{
		    nScript = -1;
		    //set button action command so it iterates through tutorial script when pressed
		    next.setActionCommand ("next2");
		    back.setActionCommand ("back2");
		    next.doClick ();
		}
		else //if tutorial not viewed
		    initGame ();
	    }
	    else
		dIntro.setText (intro [nScript]);
	}
	else if (e.getActionCommand ().equals ("back")) //previous line in intro script
	{
	    if (nScript > 0) //prevents out-of-bounds
	    {
		nScript--;
		dIntro.setText (intro [nScript]);
	    }
	}
	else if (e.getActionCommand ().equals ("next2")) //next line in tutorial script
	{
	    if (nScript < tut.length - 1) //prevents out-of-bounds
	    {
		nScript++;
		//show tutorial pic and intro
		tutPic.setIcon (createImageIcon ("tut_" + nScript + ".png"));
		dIntro.setText (tut [nScript]);
	    }
	    else
		initGame ();
	}
	else if (e.getActionCommand ().equals ("back2")) //previous line in tutorial script
	{
	    if (nScript > 0) //prevents out-of-bounds
	    {
		nScript--;
		tutPic.setIcon (createImageIcon ("tut_" + nScript + ".png"));
		dIntro.setText (tut [nScript]);
	    }
	}
	else if (e.getActionCommand ().equals ("endP")) //end phase
	{
	    disableButtons ();
	    endPhase ();
	}
	else if (e.getActionCommand ().equals ("save")) //save game
	    saveGame ();
	else if (e.getActionCommand ().equals ("mm")) //back to main menu from Game Screen
	{
	    int select = JOptionPane.showConfirmDialog (null, "Are you sure? Don't forget to save!", "Return to Main Menu", JOptionPane.YES_NO_OPTION);
	    if (select == JOptionPane.YES_OPTION)
	    {
		card.show (game, "1");
		stopMusic ();
		playMusic ("hfcyg");
	    }
	}
	else if (e.getActionCommand ().equals ("mm2")) //back to main menu from win screen
	{
	    card.show (game, "1");
	    stopMusic ();
	    playMusic ("hfcyg");
	}
	else if (e.getActionCommand ().equals ("cont")) //close dialogs and continue game
	{
	    enableButtons ();
	    bLog.dispatchEvent (new WindowEvent (bLog, WindowEvent.WINDOW_CLOSING));
	    tLog.dispatchEvent (new WindowEvent (bLog, WindowEvent.WINDOW_CLOSING));
	    bLogTA.setText ("");
	    tLogTA.setText ("");
	    endP.requestFocus ();
	}
	else //if action command is integer
	{
	    if (attacking == false) //if not attacking, set loc to location of click
		loc = Integer.parseInt (e.getActionCommand ());

	    attack.setEnabled (false); //disable attack button

	    if ((uType [loc / col] [loc % col] == 4 && pmAttackScan () == true) || pAttackScan () == true) //if attack valid, enable attack button
		attack.setEnabled (true);

	    if (moving == true) //if moving
	    {
		moveUnit (loc); //move unit to designated location
		showStatus ("kaboom running...");
	    }

	    else if (attacking == true) //if attacking
	    {
		int tLoc = Integer.parseInt (e.getActionCommand ()); //if attacking, set tLoc to location of click
		attackUnit (tLoc); //attack target, pass target location
		showStatus ("kaboom running...");
		uState [loc / col] [loc % col] = 0; //change state of unit to inactive
		action.dispatchEvent (new WindowEvent (action, WindowEvent.WINDOW_CLOSING)); //close action menu
	    }

	    else if (uAfil [loc / col] [loc % col] == (turn + 1)) //if clicked on allied unit
	    {
		if (uState [loc / col] [loc % col] == 1) //if unit is active
		{
		    action.setVisible (true); //show action menu
		    disableButtons ();
		}
	    }

	    else if (turn == 0) //if red turn
	    {
		if (mapData [loc / col] [loc % col] == 7 && uAfil [loc / col] [loc % col] == 0) //if clicked on empty red factory
		{
		    factory.setVisible (true); //show factory dialog
		    factory.requestFocus ();
		    disableButtons ();
		}
	    }

	    else //if blue turn
	    {
		if (mapData [loc / col] [loc % col] == 8 && uAfil [loc / col] [loc % col] == 0) //if clicked on empty blue factory
		{
		    factory.setVisible (true); //show factory dialog
		    factory.requestFocus ();
		    disableButtons ();
		}
	    }
	}
	soundEffect ("watch_beep_soft"); //sound effect after each click of a button
    } //end actionPerformed


    ActionListener lFactory = new ActionListener ()  //action listener for factory buttons
    {
	public void actionPerformed (ActionEvent e)
	{
	    if (e.getActionCommand ().equals ("drop")) //dropdown menu, set choice of unit
	    {
		facCB = (JComboBox) e.getSource ();
		facS = (String) facCB.getSelectedItem ();
		facChoice = 0;
		if (facS.equals ("Infantry - $500"))
		    facChoice = 1;
		else if (facS.equals ("Bazooka - $800"))
		    facChoice = 2;
		else if (facS.equals ("Tank - $1000"))
		    facChoice = 3;
		else if (facS.equals ("Missile - $1200"))
		    facChoice = 4;

		//set corresponding icons and information in displays
		iPic2.setIcon (createImageIcon ("unit_" + ((turn == 0) ? "1":
		"2") + "_" + facChoice + ".gif"));
		iPrice2.setText ("Price: $" + uPrice [facChoice]);
		iType2.setText ("Type: " + uName [facChoice]);
		if (facChoice == 4)
		{
		    iAdv2.setIcon (createImageIcon ("blank.gif"));
		    iDis2.setIcon (createImageIcon ("blank.gif"));
		}
		else
		{
		    iAdv2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
		    "1")) + "_" + uAdv [facChoice] + ".gif")));
		    iDis2.setIcon (createImageIcon ("unit_" + ((((turn == 0) ? "2":
		    "1")) + "_" + uDis [facChoice] + ".gif")));
		}
		iMov2.setIcon (createImageIcon ("stars_" + uMov [facChoice] + ".png"));
	    }

	    else if (e.getActionCommand ().equals ("confirm")) //if unit spawn choice confirm
	    {

		if (checkPrice (facChoice) == true) //if player has enough income, spawn unit
		{
		    addUnit (loc, facChoice);
		    factory.dispatchEvent (new WindowEvent (factory, WindowEvent.WINDOW_CLOSING));
		    enableButtons ();
		}
		else //tell user that he/she does not have enough income
		    JOptionPane.showMessageDialog (null, "Not enough money! ");
	    }
	    else //cancel factory spawn unit
	    {
		factory.dispatchEvent (new WindowEvent (factory, WindowEvent.WINDOW_CLOSING));
		enableButtons ();
	    }
	    soundEffect ("watch_beep_soft"); //sound effect after each click of a button
	}

    } //end factory actionListener


    ;

    ActionListener lAction = new ActionListener ()  //action listener for action menu
    {
	public void actionPerformed (ActionEvent e)
	{
	    if (e.getActionCommand ().equals ("move")) //move unit
	    {
		showStatus ("Moving...");
		disableMenu (); //do not let user click anything but cancel while moving
		disableButtons ();
		movLoc = loc;
		initMoveGrid (uType [loc / col] [loc % col]); //scan for valid movement locations
		moving = true;
	    }
	    else if (e.getActionCommand ().equals ("attack")) //attack enemy unit
	    {
		showStatus ("Attacking...");
		attacking = true;
		disableMenu (); //do not let user click anything but cancel while attacking
		disableButtons ();
		//scan for valid attack locations
		if (uType [loc / col] [loc % col] == 4)
		    mAttackScan ();
		else
		    attackScan ();
	    }
	    else if (e.getActionCommand ().equals ("wait")) //wait and finish unit turn
	    {
		action.dispatchEvent (new WindowEvent (action, WindowEvent.WINDOW_CLOSING));
		uState [loc / col] [loc % col] = 0; //set unit inactive
		mapButton [loc].setIcon (createImageIcon ("map_" + mapData [loc / col] [loc % col] + "_1_" + uType [loc / col] [loc % col] + "_0.gif")); //change unit picture to inactive

		move.setEnabled (true);
		moved = false;
		endP.setEnabled (true);
		enableButtons (); //re-enable grid buttons
	    }
	    else //cancel action
	    {
		resetMoveGrid (); //reset pathfinding values
		if (moved == true) //if has moved, reset action
		    resetAction ();
		moving = false;
		attacking = false;
		move.setEnabled (true);
		wait.setEnabled (true);
		redraw ();
		enableButtons (); //re-enable grid buttons
		action.dispatchEvent (new WindowEvent (action, WindowEvent.WINDOW_CLOSING));

	    }
	    soundEffect ("watch_beep_soft"); //sound effect after each click of a button
	}
    } //end action menu action listener


    ;
    //key listeners, activate corresponding buttons
    Action key_a = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    move.doClick ();
	}
    }


    ;
    Action key_s = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    attack.doClick ();
	}
    }


    ;
    Action key_d = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    wait.doClick ();
	}
    }


    ;
    Action key_f = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    cancel.doClick ();
	}
    }


    ;

    Action key_z = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    cont.doClick ();
	    cont2.doClick ();
	}
    }


    ;
    Action key_z_fac = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    confirm.doClick ();
	}
    }


    ;
    Action key_p = new AbstractAction ()
    {
	public void actionPerformed (ActionEvent e)
	{
	    endP.doClick ();
	}
    }


    ;
    //end keylisteners

    MouseListener mListener = new MouseListener ()  //track which grid square mouse is hovered over and displays relevant information
    {
	public void mouseEntered (MouseEvent e)  //if mouse enters button
	{
	    //get name of target button
	    Component b = e.getComponent ();
	    int i = Integer.parseInt (b.getName ());
	    //show terrain info
	    jType.setText ("Type: " + tName [mapData [i / col] [i % col]]);
	    jAfil.setText ("Team: " + afilCode [tAfil [mapData [i / col] [i % col]]]);
	    jPic.setIcon (createImageIcon ("map_" + mapData [i / col] [i % col] + "_0_0_0.gif"));
	    jDef.setIcon (createImageIcon ("stars_" + tDef [mapData [i / col] [i % col]] + ".png"));
	    jLoc.setText ("Location: (" + ((i % col) + 1) + ", " + ((i / col) + 1) + ")");
	    if (bStrength [i / col] [i % col] == 0)
		jHealth.setText ("Capture Strength: N/A");
	    else
		jHealth.setText ("Capture Strength: " + bStrength [i / col] [i % col]);

	    //show unit info if unit on square
	    if (uAfil [i / col] [i % col] != 0)
	    {
		iPic.setIcon (createImageIcon ("unit_" + uAfil [i / col] [i % col] + "_" + uType [i / col] [i % col] + ".gif"));
		iType.setText ("Type: " + uName [uType [i / col] [i % col]]);
		iTeam.setText ("Team: " + afilCode [uAfil [i / col] [i % col]]);
		if (uType [i / col] [i % col] == 4) //show no advantage or disadvantage for missile units
		{
		    iAdv.setIcon (createImageIcon ("blank.gif"));
		    iDis.setIcon (createImageIcon ("blank.gif"));
		}
		else
		{
		    iAdv.setIcon (createImageIcon ("unit_" + ((uAfil [i / col] [i % col] == 1) ? "2":
		    "1") + "_" + uAdv [uType [i / col] [i % col]] + ".gif"));
		    iDis.setIcon (createImageIcon ("unit_" + ((uAfil [i / col] [i % col] == 1) ? "2":
		    "1") + "_" + uDis [uType [i / col] [i % col]] + ".gif"));
		}
		iHealth.setIcon (createImageIcon ("health_" + uHealth [i / col] [i % col] + ".png"));
		iMov.setIcon (createImageIcon ("stars_" + uMov [uType [i / col] [i % col]] + ".png"));
	    }
	    else //if not, show blanks
	    {
		iPic.setIcon (createImageIcon ("blank.gif"));
		iType.setText ("Type:                  ");
		iTeam.setText ("Team:                  ");
		iAdv.setIcon (createImageIcon ("blank.gif"));
		iDis.setIcon (createImageIcon ("blank.gif"));
		iHealth.setIcon (createImageIcon ("blank.gif"));
		iMov.setIcon (createImageIcon ("stars_0.png"));
	    }
	}


	public void mouseClicked (MouseEvent e)
	{
	}


	public void mouseExited (MouseEvent e)
	{
	}


	public void mousePressed (MouseEvent e)
	{
	}


	public void mouseReleased (MouseEvent e)
	{
	}
    } //end mouse listener


    ;

    ActionListener tListener = new ActionListener ()  //tracks timer signals (every second)
    {
	public void actionPerformed (ActionEvent e)
	{
	    //add a second
	    second++;
	    dTimerS.setText (((second < 10) ? "0":
	    "") + second);
	    if (second == 60) //if 60 seconds, add a minute and reset seconds
	    {
		second = 0;
		minute++;
		dTimerM.setText (((minute < 10) ? "0":
		"") + minute);
	    }
	    else if (minute == 60) //if 60 minutes, add an hour and reset minutes
	    {
		minute = 0;
		hour++;
		dTimerH.setText (((hour < 10) ? "0":
		"") + hour);
	    }
	}
    } //end timer listener


    ;

    protected static ImageIcon createImageIcon (String path)  //get image file
    {
	java.net.URL imgURL = kaboom.class.getResource ("pictures/" + path);
	if (imgURL != null)
	    return new ImageIcon (imgURL);
	else
	{
	    System.err.println ("Couldn't find file: " + path);
	    return null;
	}
    } //end createImageIcon


    //play looping music
    public static void playMusic (String filepath)
    {
	try
	{
	    BGM = new AudioStream (new FileInputStream ("audio/" + filepath + ".wav")); //set song
	    MD = BGM.getData (); //get data fom song
	    loop = new ContinuousAudioDataStream (MD); //set as loop
	}


	catch (IOException error)  //error
	{
	    System.out.println ("Audio - File not found.");
	}


	MGP.start (loop); //start running loop
    } //end method playMusic


    //stop already playing music. If music is not playing, do nothing
    public static void stopMusic ()
    {
	MGP.stop (loop);
    } //end method stopMusic


    //plays non-looping sound effect

    public static void soundEffect (String filepath)
    {
	//initialize objects
	AudioPlayer SEP = AudioPlayer.player; //declare sound effect player
	AudioStream SE; //declare sound effect
	AudioData MA; //declare audio data
	AudioDataStream play = null; //set as single run (NOT LOOP)

	try
	{
	    SE = new AudioStream (new FileInputStream ("audio/" + filepath + ".wav")); //set file
	    MA = SE.getData (); //get data from file
	    play = new AudioDataStream (MA); //set data to play once (NOT LOOP)
	}


	catch (IOException error)  //error
	{
	    System.out.println ("Audio - File not found.");
	}


	SEP.start (play);
    } //end method soundEffect
}


