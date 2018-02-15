package mtg.collection.editions;

public enum Editions {

	/**
	 * Expansions
	 */

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

	tsts("5042", "Time Spiral \"Timeshifted\""),

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

	ne(""),

	mm(""),

	ud(""),

	ul(""),

	us(""),

	ex("", "Exodus"),

	sh(""),

	tp(""),

	wl(""),

	vi(""),

	mr(""),

	hl(""),

	fe(""),

	dk(""),

	lg(""),

	aq(""),

	an(""),

	/**
	 * Core Sets
	 */

	ori("5306", "Magic Origins"), 
	
	m15("5288", "Magic 2015"), 
	
	m14("5260", "Magic 2014"), 
	
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

	mpskld(""), exp(""),

	/**
	 * Conspiracy Series
	 */

	cn2("5334"), 
	
	cns("5286"),

	/**
	 * Promo Cards
	 */

	ugin(""), _15ann(""), gpx(""), pro(""), mgdc(""), wmcq(""), ptc(""), rep(""), mlp(""), sum(""), grc(""), cp(
			""), arena(""), fnmp(""), mprp(""), sus(""), hho(""), jr(""), pot(""), uqc(""), clash("", "Clash Pack"), mbp(""),

	/**
	 * Reprint Sets
	 */

	ima("5369", "Iconic Masters"), 
	
	mm3("5352", "Modern Masters 2017 Edition"), 
	
	ema("5331", "Eternal Masters"), 
	
	mm2("5304", "Modern Masters 2015 Edition"), 
	
	mma("5258", "Modern Masters"),

	/**
	 * Command Zone Series
	 */

	c16("5346"), 
	
	pca("5350"), 
	
	c15("5313"), 
	
	c14("5294"), 
	
	c13("5269"), 
	
	cma("5358"), 
	
	pc2("5236"), 
	
	cmd("5213"), 
	
	arc("5190"), 
	
	pch("5174"),

	/**
	 * From The Vault
	 */
	v16("", "From the Vault: Lore"), 
	
	v15("", "From the Vault: Angels"), 
	
	v14("", "From the Vault: Annihilation"), 
	
	v13("", "From the Vault: Twenty"), 
	
	v12("5246", "From the Vault: Realms"), 
	
	fvl(""), 
	
	fvr(""), 
	
	fve(""), 
	
	fvd(""),

	/**
	 * Duel Decks
	 */
	dds(""), ddr(""), ddq(""), ddp(""), ddadvd(""), ddaevg(""), ddagvl(""), ddajvc(""), ddo(""), ddn(""), ddm(""), ddl(
			""), ddk(""), ddj(""), ddi(""), ddh(""), ddg(""), ddf(""), pvc(""), gvl(""),

	dvd(""), jvc(""), evg("");

	private String scgLink;
	private String name;

	private Editions(final String scgLink) {
		this.scgLink = scgLink;
	}
	
	private Editions(final String scgLink, final String name) {
		this.scgLink = scgLink;
		this.name = name;
	}

	public String getScgLink() {
		return "http://sales.starcitygames.com/spoiler/display.php?&r_all=All&s%5B%5D=" + scgLink
				+ "&foil=all&g_all=All&lang%5B%5D=1&tghop=%3D&tgh=&sort1=4&sort2=1&sort3=10&sort4=0&display=3&numpage=10";
	}
	
	public String getName() {
		return name;
	}
}
