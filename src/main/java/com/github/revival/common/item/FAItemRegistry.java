package com.github.revival.common.item;

import com.github.revival.Revival;
import com.github.revival.common.FARegistry;
import com.github.revival.common.block.FABlockRegistry;
import com.github.revival.common.creativetab.FATabRegistry;
import com.github.revival.common.entity.BehaviorConfuciusornisEggDispense;
import com.github.revival.common.entity.BehaviorDodoEggDispense;
import com.github.revival.common.entity.BehaviorJavelinDispense;
import com.github.revival.common.enums.EnumDinoType;
import com.github.revival.common.handler.IContentHandler;
import com.github.revival.common.handler.LocalizationStrings;
import com.github.revival.common.item.forge.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;

public class FAItemRegistry extends FARegistry implements IContentHandler
{
    public static Item biofossil;
    public static Item AquaticScarabGem;
    public static Item relic;
    public static Item stoneboard;
    public static Item ancientSword;
    public static Item brokenSword;
    public static Item fernSeed;
    public static Item ancienthelmet;
    public static Item brokenhelmet;
    public static Item skullStick;
    public static Item gem;
    public static Item gemAxe;
    public static Item gemPickaxe;
    public static Item gemSword;
    public static Item gemHoe;
    public static Item gemShovel;
    public static Item dinoPedia;
    public static Item archNotebook;
    public static Item emptyShell;
    public static Item magicConch;
    public static Item icedMeat;
    public static Item woodjavelin;
    public static Item stonejavelin;
    public static Item ironjavelin;
    public static Item goldjavelin;
    public static Item diamondjavelin;
    public static Item ancientJavelin;
    public static Item toothDagger;
    public static Item whip;
    public static Item legBone;
    public static Item claw;
    public static Item foot;
    public static Item skull;
    public static Item brokenSapling;
    public static Item amber;
    public static Item ancientVase;
    public static Item ancientVaseBroken;
    public static Item failuresaurusFlesh;
    public static Item cultivatedChickenEgg;
    public static Item dodoEgg;
    public static Item cultivatedDodoEgg;
    public static Item confuciusornisEgg;
    public static Item cultivatedConfuciusornisEgg;
    public static Item dodoWing;
    public static Item dodoWingCooked;
    public static Item confuciornisRaw;
    public static Item confuciornisCooked;
    public static Item potteryShards;
    public static Item livingCoelacanth;
    public static Item terrorBirdEgg;
    public static Item cultivatedTerrorBirdEgg;
    public static Item terrorBirdMeat;
    public static Item terrorBirdMeatCooked;
    public static Item quaggaMeat;
    public static Item quaggaMeatCooked;
    public static Item ancientKey;
    public static Item ancientClock;
    public static Item dinosaurModels;
    public static Item armBone;
    public static Item dinoRibCage;
    public static Item vertebrae;
    public static Item skullHelmet;
    public static Item ribCage;
    public static Item femurs;
    public static Item feet;
    public static Item dnaPig;
    public static Item dnaSheep;
    public static Item dnaCow;
    public static Item dnaChicken;
    public static Item dnaSmilodon;
    public static Item dnaMammoth;
    public static Item dnaDodo;
    public static Item dnaCoelacanth;
    public static Item dnaHorse;
    public static Item dnaQuagga;
    public static Item dnaTerrorBird;
    public static Item dnaElasmotherium;
    public static Item dnaConfuciusornis;
    public static Item embryoPig;
    public static Item DominicanAmber;
    public static Item embryoSheep;
    public static Item embryoCow;
    public static Item embryoChicken;
    public static Item embryoSmilodon;
    public static Item embryoMammoth;
    public static Item embryoHorse;
    public static Item embryoQuagga;
    public static Item embryoElasmotherium;
    public static Item cookedChickenSoup;
    public static Item rawChickenSoup;
    public static Item chickenEss;
    public static Item sjl;
    public static Item cookedDinoMeat;
    public static Item fossilrecordBones;
    public static Item recordNano_Anu;
    public static Item fossilSeed;
    public static Item seed;
    public static Item fossilSeed_fern;

    public void init()
    {
        FAItemRegistry.biofossil = new ItemBioFossil();
        FAItemRegistry.DominicanAmber = new ItemDominicanAmber();
        FAItemRegistry.AquaticScarabGem = new ItemAquaticScarabGem().setUnlocalizedName("AquaticScarabGem");
        FAItemRegistry.relic = new ForgeItem("Relic_Scrap").setUnlocalizedName(LocalizationStrings.RELIC_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.stoneboard = new ItemStoneBoard();
        FAItemRegistry.ancientSword = new ItemAncientsword().setUnlocalizedName(LocalizationStrings.ANCIENT_SWORD_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.brokenSword = new ForgeItem("Broken_Ancient_Sword").setMaxStackSize(1).setUnlocalizedName(LocalizationStrings.BROKEN_SWORD_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.fernSeed = new ItemFernSeed(FABlockRegistry.ferns);
        FAItemRegistry.ancienthelmet = new ItemAncientHelmet(ItemArmor.ArmorMaterial.IRON, 3, 0).setUnlocalizedName(LocalizationStrings.ANCIENT_HELMET_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.brokenhelmet = new ForgeItem("Broken_Ancient_Helm").setMaxStackSize(1).setUnlocalizedName(LocalizationStrings.BROKEN_HELMET_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.skullStick = new ForgeItem("Skull_Stick").setUnlocalizedName(LocalizationStrings.SKULL_STICK_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.gem = new ForgeItem("Scarab_Gem").setUnlocalizedName(LocalizationStrings.SCARAB_GEM_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.gemAxe = new ForgeAxe(Revival.scarab, "Gem_Axe").setUnlocalizedName(LocalizationStrings.SCARAB_AXE_NAME).setCreativeTab(FATabRegistry.tabFTools);
        FAItemRegistry.gemPickaxe = new ForgePickaxe(Revival.scarab, "Gem_Pickaxe").setUnlocalizedName(LocalizationStrings.SCARAB_PICKAXE_NAME).setCreativeTab(FATabRegistry.tabFTools);
        FAItemRegistry.gemSword = new ForgeSword(Revival.scarab, "Gem_Sword").setUnlocalizedName(LocalizationStrings.SCARAB_SWORD_NAME).setCreativeTab(FATabRegistry.tabFCombat);

        FAItemRegistry.gemHoe = new ForgeHoe(Revival.scarab, "Gem_Hoe").setUnlocalizedName(LocalizationStrings.SCARAB_HOE_NAME).setCreativeTab(FATabRegistry.tabFTools);
        FAItemRegistry.gemShovel = new ForgeShovel(Revival.scarab, "Gem_Shovel").setUnlocalizedName(LocalizationStrings.SCARAB_SHOVEL_NAME).setCreativeTab(FATabRegistry.tabFTools);
        FAItemRegistry.dinoPedia = new ForgeItem("Dinopedia").setUnlocalizedName(LocalizationStrings.DINOPEDIA_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.emptyShell = new ForgeItem("Empty_Shell").setUnlocalizedName(LocalizationStrings.EMPTY_SHELL_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.magicConch = new ItemMagicConch().setUnlocalizedName(LocalizationStrings.MAGIC_CONCH_NAME).setCreativeTab(FATabRegistry.tabFTools);
        FAItemRegistry.icedMeat = new ItemIcedMeat(Item.ToolMaterial.EMERALD).setUnlocalizedName(LocalizationStrings.ICED_MEAT_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.amber = new ItemAmber().setUnlocalizedName(LocalizationStrings.AMBER_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.woodjavelin = new ItemJavelin(Item.ToolMaterial.WOOD, "Wooden_Javelin").setUnlocalizedName(LocalizationStrings.WOOD_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.stonejavelin = new ItemJavelin(Item.ToolMaterial.STONE, "Stone_Javelin").setUnlocalizedName(LocalizationStrings.STONE_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.ironjavelin = new ItemJavelin(Item.ToolMaterial.IRON, "Iron_Javelin").setUnlocalizedName(LocalizationStrings.IRON_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.goldjavelin = new ItemJavelin(Item.ToolMaterial.GOLD, "Gold_Javelin").setUnlocalizedName(LocalizationStrings.GOLD_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.diamondjavelin = new ItemJavelin(Item.ToolMaterial.EMERALD, "Diamond_Javelin").setUnlocalizedName(LocalizationStrings.DIAMOND_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.ancientJavelin = new ItemJavelin(Revival.scarab, "Ancient_Javelin").setUnlocalizedName(LocalizationStrings.ANCIENT_JAVELIN_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.toothDagger = new ItemToothDagger(Revival.toothDaggerMaterial).setTextureName("fossil:toothDagger").setUnlocalizedName("toothDagger").setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.whip = new ItemWhip().setUnlocalizedName(LocalizationStrings.WHIP_NAME).setCreativeTab(FATabRegistry.tabFTools);

        FAItemRegistry.legBone = new ItemDinosaurBones("legBone").setUnlocalizedName(LocalizationStrings.LEGBONE_NAME);
        FAItemRegistry.claw = new ItemDinosaurBones("uniqueItem").setUnlocalizedName(LocalizationStrings.CLAW_NAME);
        FAItemRegistry.foot = new ItemDinosaurBones("foot").setUnlocalizedName(LocalizationStrings.FOOT_NAME);
        FAItemRegistry.skull = new ItemDinosaurBones("skull").setUnlocalizedName(LocalizationStrings.SKULL_NAME);
        FAItemRegistry.armBone = new ItemDinosaurBones("armBone").setUnlocalizedName(LocalizationStrings.ARM_BONE_NAME);
        FAItemRegistry.dinoRibCage = new ItemDinosaurBones("dinoRibCage").setUnlocalizedName(LocalizationStrings.DINO_RIB_CAGE_NAME);
        FAItemRegistry.vertebrae = new ItemDinosaurBones("vertebrae").setUnlocalizedName(LocalizationStrings.VERTEBRAE_NAME);

        FAItemRegistry.brokenSapling = new ForgeItem("fossilPlant").setUnlocalizedName(LocalizationStrings.BROKEN_SAPLING_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.failuresaurusFlesh = new ForgeItem("flesh").setUnlocalizedName(LocalizationStrings.FAILURESAURUS_FLESH_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.cultivatedChickenEgg = new ItemBirdEgg(4).setUnlocalizedName("eggCultivatedChicken").setTextureName("fossil:Egg_Cultivated_Chicken");
        FAItemRegistry.dodoEgg = new ItemBirdEgg(0).setUnlocalizedName(LocalizationStrings.DODO_EGG_NAME).setTextureName("fossil:Egg_Dodo");
        FAItemRegistry.cultivatedDodoEgg = new ItemBirdEgg(1).setUnlocalizedName(LocalizationStrings.CULTIVATED_DODO_EGG_NAME).setTextureName("fossil:Egg_Cultivated_Dodo");
        FAItemRegistry.confuciusornisEgg = new ItemBirdEgg(2).setUnlocalizedName("eggConfuciusornis").setTextureName("fossil:Egg_Confuciusornis");
        FAItemRegistry.cultivatedConfuciusornisEgg = new ItemBirdEgg(3).setUnlocalizedName("eggCultivatedConfuciusornis").setTextureName("fossil:Egg_Cultivated_Confuciusornis");
        FAItemRegistry.potteryShards = new ForgeItem("PotteryShard").setUnlocalizedName(LocalizationStrings.POTTERY_SHARDS).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.livingCoelacanth = new ItemLivingCoelacanth(1).setUnlocalizedName(LocalizationStrings.LIVING_COELACANTH_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.terrorBirdEgg = new ItemTerrorBirdEgg(false).setUnlocalizedName(LocalizationStrings.TERROR_BIRD_EGG_NAME);
        FAItemRegistry.cultivatedTerrorBirdEgg = new ItemTerrorBirdEgg(true).setUnlocalizedName(LocalizationStrings.CULTIVATED_TERROR_BIRD_EGG_NAME);

        FAItemRegistry.skullHelmet = new ItemSkullHelmet(Revival.bone, 3, 0).setUnlocalizedName(LocalizationStrings.SKULL_HELMET_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.ribCage = new ItemRibCage(Revival.bone, 3, 1).setUnlocalizedName(LocalizationStrings.RIBCAGE_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.femurs = new ItemFemurs(Revival.bone, 3, 2).setUnlocalizedName(LocalizationStrings.FEMURS_NAME).setCreativeTab(FATabRegistry.tabFCombat);
        FAItemRegistry.feet = new ItemFeet(Revival.bone, 3, 3).setUnlocalizedName(LocalizationStrings.FEET_NAME).setCreativeTab(FATabRegistry.tabFCombat);

        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            EnumDinoType.values()[i].EggItem = new ItemAncientEgg(i).setUnlocalizedName("egg" + EnumDinoType.values()[i].name()).setCreativeTab(FATabRegistry.tabFMaterial);
        }

        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            EnumDinoType.values()[i].DNAItem = new ForgeItem(EnumDinoType.values()[i].name() + "_DNA").setUnlocalizedName("dna" + EnumDinoType.values()[i].name()).setCreativeTab(FATabRegistry.tabFMaterial);
        }

        FAItemRegistry.dnaPig = new ForgeItem("Pig_DNA").setUnlocalizedName(LocalizationStrings.DNA_PIG_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaSheep = new ForgeItem("Sheep_DNA").setUnlocalizedName(LocalizationStrings.DNA_SHEEP_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaCow = new ForgeItem("Cow_DNA").setUnlocalizedName(LocalizationStrings.DNA_COW_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaChicken = new ForgeItem("Chicken_DNA").setUnlocalizedName(LocalizationStrings.DNA_CHICKEN_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaSmilodon = new ForgeItem("Smilodon_DNA").setUnlocalizedName(LocalizationStrings.DNA_SMILODON_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaMammoth = new ForgeItem("Mammoth_DNA").setUnlocalizedName(LocalizationStrings.DNA_MAMMOTH_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaDodo = new ForgeItem("Dodo_DNA").setUnlocalizedName(LocalizationStrings.DNA_DODO_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaCoelacanth = new ForgeItem("Coelacanth_DNA").setUnlocalizedName(LocalizationStrings.DNA_COELACANTH_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaHorse = new ForgeItem("Horse_DNA").setUnlocalizedName(LocalizationStrings.DNA_HORSE_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaQuagga = new ForgeItem("Quagga_DNA").setUnlocalizedName(LocalizationStrings.DNA_QUAGGA_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaTerrorBird = new ForgeItem("TerrorBird/TerrorBird_DNA").setUnlocalizedName(LocalizationStrings.DNA_TERROR_BIRD_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaElasmotherium = new ForgeItem("Elasmotherium/Elasmotherium_DNA").setUnlocalizedName(LocalizationStrings.DNA_ELASMOTHERIUM_NAME).setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.dnaConfuciusornis = new ForgeItem("Confuciusornis_DNA").setUnlocalizedName("dnaConfuciusornis").setCreativeTab(FATabRegistry.tabFMaterial);

        FAItemRegistry.embryoPig = new ItemEmbryoSyringe(0).setUnlocalizedName(LocalizationStrings.EMBRYO_PIG_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoSheep = new ItemEmbryoSyringe(1).setUnlocalizedName(LocalizationStrings.EMBRYO_SHEEP_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoCow = new ItemEmbryoSyringe(2).setUnlocalizedName(LocalizationStrings.EMBRYO_COW_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoChicken = new ItemEmbryoSyringe(3).setUnlocalizedName(LocalizationStrings.EMBRYO_CHICKEN_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoSmilodon = new ItemEmbryoSyringe(4).setUnlocalizedName(LocalizationStrings.EMBRYO_SMILODON_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoMammoth = new ItemEmbryoSyringe(5).setUnlocalizedName(LocalizationStrings.EMBRYO_MAMMOTH_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoHorse = new ItemEmbryoSyringe(6).setUnlocalizedName(LocalizationStrings.EMBRYO_HORSE_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoQuagga = new ItemEmbryoSyringe(7).setUnlocalizedName(LocalizationStrings.EMBRYO_QUAGGA_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.embryoElasmotherium = new ItemEmbryoSyringe(8).setUnlocalizedName(LocalizationStrings.EMBRYO_ELASMOTHERIUM_NAME).setCreativeTab(FATabRegistry.tabFItems);
        FAItemRegistry.ancientClock = new Item().setTextureName("apple_golden").setUnlocalizedName("ancientClock").setCreativeTab(FATabRegistry.tabFItems).setMaxStackSize(1);
        FAItemRegistry.ancientKey = new ForgeItem("Ancient_Key").setUnlocalizedName("ancientKey").setCreativeTab(FATabRegistry.tabFItems);

        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            EnumDinoType.values()[i].DropItem = new ForgeFood(3, 0.3F, true, EnumDinoType.values()[i].name() + "_Meat").setUnlocalizedName("raw" + EnumDinoType.values()[i].name()).setCreativeTab(FATabRegistry.tabFFood);
        }

        FAItemRegistry.cookedDinoMeat = new ForgeFood(8, 0.8F, true, "Dino_Steak").setUnlocalizedName(LocalizationStrings.DINO_STEAK_NAME).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.cookedChickenSoup = new ForgeItem("Cooked_Chicken_Soup").setUnlocalizedName(LocalizationStrings.COOKED_CHICKEN_SOUP_NAME).setMaxStackSize(1).setContainerItem(Items.bucket).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.rawChickenSoup = new ForgeItem("Raw_Chicken_Soup").setUnlocalizedName(LocalizationStrings.RAW_CHICKEN_SOUP_NAME).setMaxStackSize(1).setContainerItem(Items.bucket).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.chickenEss = new ItemChickenEss(10, 0.0F, false, "Essence_Of_Chicken").setUnlocalizedName(LocalizationStrings.EOC_NAME).setContainerItem(Items.glass_bottle).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.sjl = new ForgeFood(8, 2.0F, false, "Sio_Chiu_Le").setUnlocalizedName(LocalizationStrings.SJL_NAME).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.dodoWing = new ForgeFood(3, 0.3F, false, "Raw_Dodo_Wing").setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName(LocalizationStrings.DODO_WING_NAME).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.dodoWingCooked = new ForgeFood(8, 0.8F, false, "Cooked_Dodo_Wing").setUnlocalizedName(LocalizationStrings.DODO_WING_COOKED_NAME).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.confuciornisRaw = new ForgeFood(2, 0.3F, false, "RawConfuciornis").setUnlocalizedName("rawConfuciornis").setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.confuciornisCooked = new ForgeFood(5, 0.8F, false, "CookedConfuciornis").setUnlocalizedName("cookedConfuciornis").setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.terrorBirdMeat = new ForgeFood(2, 0.8F, false, "TerrorBird/TerrorBird_Meat").setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName(LocalizationStrings.TERROR_BIRD_MEAT).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.terrorBirdMeatCooked = new ForgeFood(4, 0.8F, false, "TerrorBird/TerrorBird_Meat_Cooked").setUnlocalizedName(LocalizationStrings.TERROR_BIRD_MEAT_COOKED).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.quaggaMeat = new ForgeFood(2, 0.8F, false, "Quagga_Meat").setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName(LocalizationStrings.QUAGGA_MEAT).setCreativeTab(FATabRegistry.tabFFood);
        FAItemRegistry.quaggaMeatCooked = new ForgeFood(7, 1F, false, "Quagga_Meat_Cooked").setUnlocalizedName(LocalizationStrings.QUAGGA_MEAT_COOKED).setCreativeTab(FATabRegistry.tabFFood);

        FAItemRegistry.fossilrecordBones = new ItemFossilRecord(LocalizationStrings.RECORD_BONES_NAME, "fossil:record_bones").setUnlocalizedName(LocalizationStrings.FOSSIL_RECORD_NAME);
        FAItemRegistry.recordNano_Anu = new ItemFossilRecord(LocalizationStrings.FOSSIL_RECORD_NANO_ANU, "fossil:record_anu").setUnlocalizedName(LocalizationStrings.RECORD_BONES_NAME);

        FAItemRegistry.fossilSeed_fern = new ForgeItem("plants/fossilSeed_fern").setUnlocalizedName("fossilSeed_fern").setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.fossilSeed = new ItemFossilSeeds(true).setUnlocalizedName("fossilSeed").setCreativeTab(FATabRegistry.tabFMaterial);
        FAItemRegistry.seed = new ItemFossilSeeds(false).setUnlocalizedName("seed").setCreativeTab(FATabRegistry.tabFMaterial);

        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.ancientJavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), -1));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.woodjavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), 0));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.stonejavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), 1));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.ironjavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), 2));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.diamondjavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), 3));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.goldjavelin, new BehaviorJavelinDispense(MinecraftServer.getServer(), 4));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.dodoEgg, new BehaviorDodoEggDispense(MinecraftServer.getServer(), 5));
        BlockDispenser.dispenseBehaviorRegistry.putObject(FAItemRegistry.confuciusornisEgg, new BehaviorConfuciusornisEggDispense(MinecraftServer.getServer(), 6));

        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            GameRegistry.registerItem(EnumDinoType.values()[i].DNAItem, "dna" + EnumDinoType.values()[i].name());
        }
        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            GameRegistry.registerItem(EnumDinoType.values()[i].EggItem, "egg" + EnumDinoType.values()[i].name());
        }
        for (int i = 0; i < EnumDinoType.values().length; i++)
        {
            GameRegistry.registerItem(EnumDinoType.values()[i].DropItem, "raw" + EnumDinoType.values()[i].name());
        }
    }

    public void initCreativeTabs()
    {

    }

    public void gameRegistry() throws Exception
    {
        initCreativeTabs();
        try
        {
            for (Field f : getClass().getDeclaredFields())
            {
                Object obj = f.get(null);
                if (obj instanceof Item)
                    registerItem((Item) obj);
                else if (obj instanceof Item[])
                    for (Item item : (Item[]) obj)
                        registerItem(item);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void registerItem(Item item)
    {
        String name = item.getUnlocalizedName();
        String[] strings = name.split("\\.");
        name = strings[strings.length - 1];

        GameRegistry.registerItem(item, name);
    }
}
