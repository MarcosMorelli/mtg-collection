package mtg.collection.editions;

import java.util.ArrayList;
import java.util.List;

public enum Editions {

	/**
	 * Expansions
	 */
	
	rna("5400", "Ravnica Allegiance"),
	
	grn("5392", "Guilds of Ravnica"),
	
	dom("5379", "Dominaria"),

	rix("5375", "Rivals of Ixalan"),

	xln("5366", "Ixalan"),

	hou("5360", "Hour of Devastation"),

	akh("5355", "Amonkhet"),

	aer("5344", "Aether Revolt"),

	kld("5339", "Kaladesh"),

	emn("5336", "Eldritch Moon"),

	soi("5329", "Shadows over Innistrad"),

	ogw("5315", "Oath of the Gatewatch"),

	bfz("5308", "Battle for Zendikar"),

	dtk("5302", "Dragons of Tarkir"),

	frf("5296", "Fate Reforged"),

	ktk("5291", "Khans of Tarkir"),

	jou("5281", "Journey into Nyx"),

	bng("5271", "Born of the Gods"),

	ths("5266", "Theros"),

	dgm("5254", "Dragon's Maze"),

	gtc("5249", "Gatecrash"),

	rtr("5243", "Return to Ravnica"),

	avr("5228", "Avacyn Restored"),

	dka("5221", "Dark Ascension"),

	isd("5215", "Innistrad"),

	nph("5207", "New Phyrexia"),

	mbs("5202", "Mirrodin Besieged"),

	som("5197", "Scars of Mirrodin"),

	roe("5187", "Rise of the Eldrazi"),

	wwk("5177", "Worldwake"),

	zen("5172", "Zendikar"),

	arb("5131", "Alara Reborn"),

	cfx("5116", "Conflux"),

	ala("5106", "Shards of Alara"),

	eve("5096", "Eventide"),

	shm("5094", "Shadowmoor"),

	mt("5083", "Morningtide"),

	lw("5064", "Lorwyn"),

	fut("5055", "Future Sight"),

	pc("5049", "Planar Chaos"),

	ts("5042", "Time Spiral"),

	cs("5040", "Coldsnap"),

	ai("1012", "Alliances"),

	ia("1010", "Ice Age"),

	di("5037", "Dissension"),

	gp("5035", "Guildpact"),

	rav("5026", "Ravnica: City of Guilds"),

	sok("5020", "Saviors of Kamigawa"),

	bok("5018", "Betrayers of Kamigawa"),

	chk("5005", "Champions of Kamigawa"),

	_5dn("5007", "Fifth Dawn"),

	ds("1057", "Darksteel"),

	mi("1055", "Mirrodin"),

	sc("1051", "Scourge"),

	le("1049", "Legions"),

	on("1047", "Onslaught"),

	ju("1045", "Judgment"),

	tr("1043", "Torment"),

	od("1041", "Odyssey"),

	ap("1039", "Apocalypse"),

	ps("1035", "Planeshift"),

	in("1033", "Invasion"),

	pr("1031", "Prophecy"),

	ne("1029", "Nemesis"),

	mm("1027", "Mercadian Masques"),

	ud("1023", "Urza's Destiny"),

	ul("1021", "Urza's Legacy"),

	us("1020", "Urza's Saga"),

	ex("1019", "Exodus"),

	sh("1018", "Stronghold"),

	tp("1017", "Tempest"),

	wl("1016", "Weatherlight"),

	vi("1014", "Visions"),

	mr("1013", "Mirage"),

	hl("1011", "Homelands"),

	fe("1008", "Fallen Empires"),

	dk("1007", "The Dark"),

	lg("1006", "Legends"),

	aq("1005", "Antiquities"),

	an("1004", "Arabian Nights"),

	/**
	 * Core Sets
	 */
	
	m19("5384", "Core Set 2019"),

	ori("5306", "Magic Origins"),

	m15("5288", "Magic 2015"),

	m14("5260", "Magic 2014 Core Set"),

	m13("5241", "Magic 2013"),

	m12("5211", "Magic 2012"),

	m11("5192", "Magic 2011"),

	m10("5137", "Magic 2010"),

	_10e("5061", "Tenth Edition"),

	_9e("5023", "Ninth Edition"),

	_8e("1053", "Eighth Edition"),

	_7e("1037", "Seventh Edition"),

	_6e("1025", "Classic Sixth Edition"),

	_5e("1015", "Fifth Edition"),

	_4e("1009", "Fourth Edition"),

	rv("1003", "Revised Edition"),

	un("1002", "Unlimited Edition"),

	be("1001", "Limited Edition Beta"),

	al("1000", "Limited Edition Alpha"),

	/**
	 * Masterpiece Series
	 */
	
	topper("5402", "Ultimate Masters: Topper"),

	mpsakh("5357", "Amonkhet Invocations"),

	mpskld("5343", "Kaladesh Inventions"),

	exp("5312", "Zendikar Expeditions"),

	/**
	 * Conspiracy Series
	 */

	cn2("5334", "Conspiracy: Take the Crown"),

	cns("5286", "Conspiracy"),

	/**
	 * Promo Cards
	 */
	
	store("0000", "Store Championship", "Store Championship"),
	
	draft("0000", "Draft Weekend", "Draft Weekend"),
	
	open("0000", "Open House", "Open House)"),
	
	graveborn("5220", "Premium Deck Series: Graveborn"),

	fire_light("5201", "Premium Deck Series: Fire & Lightning"),
	
	bab("0000", "Buy-a-Box", "Buy-a-Box"),

	sdcc("0000", "SDCC", "(SDCC"),

	intro("0000", "Intro Pack", "Intro Pack)"),

	ugin("0000", "Ugin's Fate", "(Ugin's Fate)"),

	gpx("0000", "Grand Prix", "(Grand Prix)"),

	pro("0000", "Pro Tour", "(Pro Tour)"),

	mgdc("0000", "Magic Game Day Cards", "Game Day)"),

	wmcq("0000", "World Magic Cup Qualifiers", "(WMC Qualifier)"),

	ptc("5333", "Prerelease & Launch"),

	gateway("0000", "Gateway", "(Gateway)"),

	cp("0000", "Champs", "(Champs"),

	arena("0000", "Arena League", "(Arena)"),

	fnmp("0000", "Friday Night Magic", "(FNM"),

	mprp("0000", "Magic Player Rewards", "(Player Rewards)"),

	mss("0000", "Magic Scholarship Series", "MSS"),
	
	jss("0000", "Junior Super Series", "JSS"),

	jr("0000", "Judge Gift Program", "(Judge)"),

	clash("0000", "Clash Pack", "Clash Pack)"),

	rptq("0000", "Regional PTQ", "(Regional PTQ)"),
	
	wpn("0000", "WPN", "(WPN)"),
	
	gbox("0000", "Gift Box", "Gift Box)"),
	
	duels("0000", "Duels of the Planeswalkers", "(Duels of the Planeswalker"),
	
	convention("0000", "Convention", "(Convention"),
	
	idw("0000", "IDW Comics", "(IDW Comics)"),
	
	dragonfury("0000", "Tarkir Dragonfury", "(Dragons of Tarkir Dragonfury Game)"),

	/**
	 * Reprint Sets
	 */
	
	uma("5403", "Ultimate Masters"),

	m25("5377", "Masters 25"),

	ima("5369", "Iconic Masters"),

	mm3("5352", "Modern Masters 2017 Edition"),

	ema("5331", "Eternal Masters"),

	mm2("5304", "Modern Masters 2015 Edition"),

	mma("5258", "Modern Masters"),
	
	chr("1062", "Chronicles"),

	/**
	 * Beginner Sets
	 */

	st("1063", "Starter 1999"),

	p3k("1061", "Portal Three Kingdoms"),

	po2("1060", "Portal Second Age"),

	po("1059", "Portal"),

	/**
	 * Command Zone Series
	 */
	
	c18("5391", "Commander 2018"),

	c17("5364", "Commander 2017"),

	cma("5358", "Commander Anthology"),

	c16("5346", "Commander 2016"),

	pca("5350", "Planechase Anthology"),

	c15("5313", "Commander 2015"),

	c14("5294", "Commander 2014 Edition"),

	c13("5269", "Commander 2013 Edition"),

	pc2("5236", "Planechase 2012 Edition"),

	cmd("5213", "Commander"),

	arc("5190", "Archenemy"),

	pch("5174", "Planechase"),

	/**
	 * From The Vault
	 */
	
	spell_jace("5388", "Signature Spellbook: Jace"),

	v17("5374", "From the Vault: Transform"),

	v16("5342", "From the Vault: Lore"),

	v15("5310", "From the Vault: Angels"),

	v14("5293", "From the Vault: Annihilation"),

	v13("5268", "From the Vault: Twenty"),

	v12("5246", "From the Vault: Realms"),

	fvl("5219", "From the Vault: Legends"),

	fvr("5194", "From the Vault: Relics"),

	fve("5171", "From the Vault: Exiled"),

	fvd("5108", "From the Vault: Dragons"),

	/**
	 * Duel Decks
	 */
	ddt("5368", "Duel Decks: Merfolk vs. Goblins"),

	dds("5354", "Duel Decks: Mind vs. Might"),

	ddr("5341", "Duel Decks: Nissa vs. Ob Nixilis"),

	ddq("5327", "Duel Decks: Blessed vs. Cursed"),

	ddp("5311", "Duel Decks: Zendikar vs. Eldrazi"),

	ddo("5301", "Duel Decks: Kiora vs. Elspeth"),

	ddn("5290", "Duel Decks: Speed vs. Cunning"),

	ddm("5280", "Duel Decks: Jace vs. Vraska"),

	ddl("5265", "Duel Decks: Heroes vs. Monsters"),

	ddk("5253", "Duel Decks: Sorin vs. Tibalt"),

	ddj("5245", "Duel Decks: Izzet vs. Golgari"),

	ddi("5225", "Duel Decks: Venser vs. Koth"),

	ddh("5217", "Duel Decks: Ajani vs. Nicol Bolas"),

	ddg("5209", "Duel Decks: Knights vs. Dragons"),

	ddf("5195", "Duel Decks: Elspeth vs. Tezzeret"),

	pvc("5189", "Duel Decks: Phyrexia vs. The Coalition"),

	gvl("5176", "Duel Decks: Garruk vs. Liliana"),

	dvd("5134", "Duel Decks: Divine vs. Demonic"),

	jvc("5115", "Duel Decks: Jace vs. Chandra"),

	evg("5082", "Duel Decks: Elves vs. Goblins");

	private String scgCode;
	private String name;
	private List<String> scgPromoNames;

	private Editions(final String scgCode) {
		this.scgCode = scgCode;
	}

	private Editions(final String scgCode, final String name) {
		this(scgCode);
		this.name = name;
	}

	private Editions(final String scgCode, final String name, final String... scgPromoNames) {
		this(scgCode, name);
		this.scgPromoNames = new ArrayList<String>();
		for (int i = 0; i < scgPromoNames.length; i++) {
			this.scgPromoNames.add(scgPromoNames[i]);
		}
	}

	public String getScgLink(final int index) {
		if (scgCode.equals("0000")) {
			return "http://sales.starcitygames.com//spoiler/display.php?name=" + getScgPromoName(index)
					+ "&namematch=EXACT&textmatch=AND&c_all=All&colormatch=OR&colorexclude=1&card_type_match=OR&crittermatch=OR"
					+ "&r_all=All&foil=all&g_all=All&lang%5B%5D=1&sort1=4&sort2=1&sort3=10&sort4=0&display=3&numpage=10";
		}

		return "http://sales.starcitygames.com/spoiler/display.php?&r_all=All&s%5B%5D=" + scgCode
				+ "&foil=all&g_all=All&lang%5B%5D=1&tghop=%3D&tgh=&sort1=4&sort2=1&sort3=10&sort4=0&display=3&numpage=10";
	}

	public String getScgCode() {
		return scgCode;
	}

	public String getName() {
		return name;
	}

	public int getScgPromoNamesListSize() {
		if (scgCode.equals("0000")) {
			return scgPromoNames.size();
		} else {
			return 1;
		}
	}

	public String getScgPromoName() {
		return scgPromoNames.get(0);
	}

	public String getScgPromoName(final int index) {
		return scgPromoNames.get(index).replaceAll("\\(", "%28").replaceAll("\\)", "%29").replaceAll(" ", "%20");
	}

	public String getFileName() {
		return "editions/" + toString().replaceAll("_", "");
	}
}
