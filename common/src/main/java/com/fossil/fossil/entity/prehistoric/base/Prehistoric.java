package com.fossil.fossil.entity.prehistoric.base;

import com.fossil.fossil.Fossil;
import com.fossil.fossil.block.IDinoUnbreakable;
import com.fossil.fossil.entity.ai.DinoAIMating;
import com.fossil.fossil.entity.util.EntityToyBase;
import com.fossil.fossil.item.ModItems;
import com.fossil.fossil.util.*;
import dev.architectury.extensions.network.EntitySpawnExtension;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;

import java.util.*;
import java.util.function.Predicate;

public abstract class Prehistoric extends TamableAnimal implements IPrehistoricAI, PlayerRideableJumping, EntitySpawnExtension, IAnimatable {

    private static final EntityDataAccessor<Integer> AGETICK = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HUNGER = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> MODELIZED = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLEEING = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SUBSPECIES = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MOOD = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> OWNERDISPLAYNAME = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> AGINGDISABLED = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> HOLDING_IN_MOUTH = SynchedEntityData.defineId(Prehistoric.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<FossilAnimations> CURRENT_ANIMATION = SynchedEntityData.defineId(Prehistoric.class, FossilAnimations.SERIALIZER);
    private Gender gender; // should be effectively final
    private static final Predicate<Entity> IS_PREHISTORIC = entity -> entity instanceof Prehistoric;
    // public final Animation SPEAK_ANIMATION;
    public final double baseDamage;
    public final double maxDamage;
    public final double baseHealth;
    public final double maxHealth;
    public final double baseSpeed;
    public final double maxSpeed;
    public final double baseArmor;
    public final double maxArmor;
    // public Animation ATTACK_ANIMATION;
    public final float minSize;
    public final float maxSize;
    public final int teenAge;
    public final int adultAge;
    public OrderType currentOrder;
    public boolean hasFeatherToggle = false;
    public boolean featherToggle;
    public boolean hasTeenTexture = false;
    public boolean hasBabyTexture;
    public float weakProgress;
    public float sitProgress;
    public int ticksSat;
    public float sleepProgress;
    public float climbProgress;
    public int ticksSlept;
    public float pediaScale;
    public int pediaY = 0;
    public int ticksTillPlay;
    public int ticksTillMate;
    public boolean isDaytime;
    public float ridingXZ;
    public float ridingY = 1;
    public float actualWidth;
    public boolean shouldWander = true;
    protected boolean developsResistance;
    protected boolean breaksBlocks;
    protected int nearByMobsAllowed;
    protected float jumpPower;
    protected boolean horseJumping;
    // private Animation currentAnimation;
    private boolean droppedBiofossil = false;
    private int animTick;
    private int fleeTicks = 0;
    private int moodCheckCooldown = 0;
    private int cathermalSleepCooldown = 0;
    private int ticksClimbing = 0;
    private float eggScale = 1.0F;
    public TimePeriod timePeriod;
    public Diet diet;
    public Item cultivatedEggItem;
    public Item uncultivatedEggItem;
    public final boolean isCannibalistic;
    public ResourceLocation textureLocation;

    public Prehistoric(
            EntityType<? extends Prehistoric> entityType,
            Level level,
            boolean isCannibalistic,
            float minSize,
            float maxSize,
            int teenAge,
            int adultAge,
            double baseDamage,
            double maxDamage,
            double baseHealth,
            double maxHealth,
            double baseSpeed,
            double maxSpeed,
            double baseArmor,
            double maxArmor
    ) {
        super(entityType, level);
        this.setHunger(this.getMaxHunger() / 2);
        this.hasBabyTexture = true;
        this.pediaScale = 1.0F;
        this.nearByMobsAllowed = 15;
        this.currentOrder = OrderType.WANDER;
        if (ticksTillMate == 0) {
            ticksTillMate = this.random.nextInt(6000) + 6000;
        }
        this.isCannibalistic = isCannibalistic;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.teenAge = teenAge;
        this.adultAge = adultAge;
        this.baseDamage = baseDamage;
        this.maxDamage = maxDamage;
        this.baseHealth = baseHealth;
        this.maxHealth = maxHealth;
        this.baseSpeed = baseSpeed;
        this.maxSpeed = maxSpeed;
        this.baseArmor = baseArmor;
        this.maxArmor = maxArmor;
        this.updateAbilities();
        if (this.getMobType() == MobType.WATER) {
            this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
            this.getNavigation().getNodeEvaluator().setCanFloat(true);
        }
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 2D);
    }


    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return this.getType().getDimensions();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.targetSelector.addGoal(2, new DinoAIMating(this));
    }

    public static boolean isEntitySmallerThan(Entity entity, float size) {
        if (entity instanceof Prehistoric prehistoric) {
            return prehistoric.getActualWidth() <= size;
        } else {
            return entity.getBbWidth() <= size;
        }
    }

    /**
     * Do things before {@code  LivingEntity#knockBack}
     * This is supposed to launch up any entities always
     * @return newly updated strength value
     */
    public static double beforeKnockBack(LivingEntity entity, double strength, double x, double y) {
        double resistance = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
        double reversed = 1 - resistance;
        entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.4 * reversed + 0.1, 0));
        return strength * 2.0;
    }

    public static void knockBackMob(Entity entity, double xMotion, double yMotion, double zMotion) {
        double horizontalSpeed = Math.sqrt(xMotion * xMotion + zMotion * zMotion);
        Vec3 knockBack = entity.getDeltaMovement().scale(0.5).add(
                -xMotion / horizontalSpeed,
                yMotion,
                -zMotion / horizontalSpeed
        );
        entity.setDeltaMovement(knockBack);
        entity.hurtMarked = true;
    }

    public static boolean canBreak(Block block) {
        if (block instanceof IDinoUnbreakable) return false;
        BlockState state = block.defaultBlockState();
        if (!state.requiresCorrectToolForDrops()) return false;
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return false;
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGETICK, 0);
        this.entityData.define(HUNGER, 0);
        this.entityData.define(MODELIZED, false);
        this.entityData.define(ANGRY, false);
        this.entityData.define(FLEEING, false);
        this.entityData.define(SUBSPECIES, 0);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(MOOD, 0);
        this.entityData.define(OWNERDISPLAYNAME, "");
        this.entityData.define(AGINGDISABLED, false);
        this.entityData.define(HOLDING_IN_MOUTH, ItemStack.EMPTY);
    }

    @Override
    public void saveAdditionalSpawnData(FriendlyByteBuf buf) {
        buf.writeBoolean(getGender() == Gender.MALE);
    }

    @Override
    public void loadAdditionalSpawnData(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            gender = Gender.MALE;
        } else {
            gender = Gender.FEMALE;
        }
        refreshTexturePath();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AgeTick", this.getAgeInTicks());
        compound.putInt("Hunger", this.getHunger());
        compound.putBoolean("isModelized", this.isSkeleton());
        compound.putBoolean("Angry", this.isAngry());
        compound.putBoolean("DinoTamed", this.isTame());
        compound.putBoolean("Fleeing", this.isFleeing());
        compound.putInt("SubSpecies", this.getSubSpecies());
        compound.putString("Gender", this.gender.toString());
        compound.putInt("Mood", this.getMood());
        compound.putInt("TicksTillPlay", this.ticksTillPlay);
        compound.putInt("TicksSlept", this.ticksSlept);
        compound.putInt("TicksTillMate", this.ticksTillMate);
        compound.putInt("TicksClimbing", this.ticksClimbing);
        compound.putByte("currentOrder", (byte) this.currentOrder.ordinal());
        compound.putString("OwnerDisplayName", this.getOwnerDisplayName());
        compound.putFloat("YawRotation", this.yBodyRot);
        compound.putFloat("HeadRotation", this.yHeadRot);
        compound.putBoolean("AgingDisabled", this.isAgingDisabled());
        compound.putInt("CathermalTimer", this.cathermalSleepCooldown);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAgeinTicks(compound.getInt("AgeTick"));
        this.setHunger(compound.getInt("Hunger"));
        this.setFleeing(compound.getBoolean("Fleeing"));
        this.setSkeleton(compound.getBoolean("isModelized"));
        this.setAngry(compound.getBoolean("Angry"));
        this.setTame(compound.getBoolean("DinoTamed"));
        this.setSubSpecies(compound.getInt("SubSpecies"));
        if ("female".equalsIgnoreCase(compound.getString("Gender"))) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.MALE;
        }
        this.setSleeping(compound.getBoolean("Sleeping"));
        this.setOrderedToSit(compound.getBoolean("Sitting"));
        this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
        this.setMood(compound.getInt("Mood"));
        if (compound.contains("currentOrder")) {
            this.setOrder(OrderType.values()[compound.getByte("currentOrder")]);
        }
        this.ticksTillPlay = compound.getInt("TicksTillPlay");
        this.ticksClimbing = compound.getInt("TicksClimbing");
        this.ticksTillMate = compound.getInt("TicksTillMate");
        this.ticksSlept = compound.getInt("TicksSlept");
        this.yBodyRot = compound.getInt("YawRotation");
        this.yHeadRot = compound.getInt("HeadRotation");
        if (compound.contains("Owner", 8)) {
            String s = compound.getString("Owner");
            this.setOwnerDisplayName(s);
        } else {
            this.setOwnerDisplayName(compound.getString("OwnerDisplayName"));
        }
        this.cathermalSleepCooldown = compound.getInt("CathermalTimer");
        refreshTexturePath();
    }

    public String getOwnerDisplayName() {
        return this.entityData.get(OWNERDISPLAYNAME);
    }

    public void setOwnerDisplayName(String displayName) {
        this.entityData.set(OWNERDISPLAYNAME, displayName);
    }

    public AABB getAttackBounds() {
        float size = (float) (this.getBoundingBox().getSize() * 0.25F);
        return this.getBoundingBox().inflate(2.0F + size, 2.0F + size, 2.0F + size);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor levelIn,
            DifficultyInstance difficultyIn,
            MobSpawnType reason,
            @Nullable SpawnGroupData spawnDataIn,
            @Nullable CompoundTag dataTag
    ) {
        spawnDataIn = super.finalizeSpawn(levelIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAgeInDays(this.adultAge);
        this.setSpawnValues();
        this.updateAbilities();
        ticksTillPlay = 0;
        ticksTillMate = 24000;
        this.onGround = true;
        this.heal(this.getMaxHealth());
        this.currentOrder = OrderType.WANDER;
        this.grow(0);
        this.setNoAi(false);
        if (gender == null) gender = Gender.random(random);

        return spawnDataIn;
    }

    @Override
    public boolean isNoAi() {
        return this.isSkeleton() || super.isNoAi();
    }

    public void doPlayBonus(int playBonus) {
        if (ticksTillPlay == 0) {
            this.setMood(this.getMood() + playBonus);
            if (!this.level.isClientSide) {
                //TODO
                // Revival.sendMSGToAll(new MessageHappyParticles(this.getEntityId()));
            }
            ticksTillPlay = this.random.nextInt(600) + 600;
        }
    }

    public abstract void setSpawnValues();

    public OrderType getOrderType() {
        return this.currentOrder;
    }

    @Override
    public boolean isImmobile() {
        return this.getHealth() <= 0.0F ||
                isOrderedToSit() ||
                this.isSkeleton() ||
                this.isActuallyWeak() ||
                this.isVehicle() ||
                this.isSleeping();
    }

    @Override
    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
        if (!sleeping) {
            cathermalSleepCooldown = 10000 + random.nextInt(6000);
        }
        refreshTexturePath();
    }

    public BlockPos getBlockToEat(int range) {
        BlockPos pos;

        for (int r = 1; r <= range; r++) {
            for (int ds = -r; ds <= r; ds++) {
                for (int dy = 4; dy > -5; dy--) {
                    int x = Mth.floor(this.getX() + ds);
                    int y = Mth.floor(this.getY() + dy);
                    int z = Mth.floor(this.getZ() - r);
                    if (this.getY() + dy >= 0 &&
                            this.getY() + dy <= this.level.getHeight() &&
                            FoodMappings.getBlockFoodAmount(this.level.getBlockState(new BlockPos(x, y, z)).getBlock(), diet) != 0
                    ) {
                        pos = new BlockPos(x, y, z);
                        return pos;
                    }

                    if (this.getY() + dy >= 0 &&
                            this.getY() + dy <= this.level.getHeight() &&
                            FoodMappings.getBlockFoodAmount(this.level.getBlockState(new BlockPos(x, y, z)).getBlock(), diet) != 0
                    ) {
                        pos = new BlockPos(x, y, z);
                        return pos;
                    }
                }
            }

            for (int ds = -r + 1; ds <= r - 1; ds++) {
                for (int dy = 4; dy > -5; dy--) {
                    int x = Mth.floor(this.getX() + ds);
                    int y = Mth.floor(this.getY() + dy);
                    int z = Mth.floor(this.getZ() - r);

                    if (this.getY() + dy >= 0 &&
                            this.getY() + dy <= this.level.getHeight() &&
                            FoodMappings.getBlockFoodAmount(this.level.getBlockState(new BlockPos(x, y, z)).getBlock(), diet) != 0
                    ) {
                        pos = new BlockPos(x, y, z);
                        return pos;
                    }

                    if (this.getY() + dy >= 0 &&
                            this.getY() + dy <= this.level.getHeight() &&
                            FoodMappings.getBlockFoodAmount(this.level.getBlockState(new BlockPos(x, y, z)).getBlock(), diet) != 0
                    ) {
                        pos = new BlockPos(x, y, z);
                        return pos;
                    }
                }
            }
        }

        return null;
    }

    public void setOrder(OrderType newOrder) {
        this.currentOrder = newOrder;
    }

    /*public TileEntityFeeder getNearestFeeder(int feederRange) {
        for (int dx = -2; dx != -(feederRange + 1); dx += (dx < 0) ? (dx * -2) : (-(2 * dx + 1))) {
            for (int dy = -5; dy < 4; dy++) {
                for (int dz = -2; dz != -(feederRange + 1); dz += (dz < 0) ? (dz * -2) : (-(2 * dz + 1))) {
                    if (this.getY() + dy >= 0 && this.getY() + dy <= this.level.getHeight()) {
                        TileEntity feeder = this.level.getTileEntity(new BlockPos(Mth.floor(this.getX() + dx), Mth.floor(this.getY() + dy), Mth.floor(this.getZ() + dz)));

                        if (feeder instanceof TileEntityFeeder && !((TileEntityFeeder) feeder).isEmpty(dinoType)) {
                            return (TileEntityFeeder) feeder;
                        }
                    }
                }
            }
        }

        return null;
    }*/
    // TODO ^s

    public float getActualWidth() {
        return this.actualWidth * this.getAgeScale();
    }

    public boolean arePlantsNearby(int range) {
        for (int i = Mth.floor(this.getX() - range); i < Mth.ceil(this.getX() + range); i++) {
            for (int j = Mth.floor(this.getY() - range / 2.0); j < Mth.ceil(this.getY() + range / 2.0); j++) {
                for (int k = Mth.floor(this.getZ() - range); k < Mth.ceil(this.getZ() + range); k++) {
                    if (j <= this.level.getHeight() + 1D && isPlantBlock(this.level.getBlockState(new BlockPos(i, j, k)))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPushable() {
        return !this.isSkeleton() && super.isPushable();
    }

    public boolean isPlantBlock(BlockState block) {
        return block.getMaterial() == Material.CACTUS ||
                block.getMaterial() == Material.PLANT ||
                block.getMaterial() == Material.LEAVES ||
                block.getMaterial() == Material.MOSS ||
                block.getMaterial() == Material.VEGETABLE;
    }

    public boolean canSleep() {
        if (this.aiActivityType() == PrehistoricEntityTypeAI.Activity.DIURINAL) {
            return !this.isDaytime();
        } else if (this.aiActivityType() == PrehistoricEntityTypeAI.Activity.NOCTURNAL) {
            return this.isDaytime() && !this.level.canSeeSky(this.blockPosition().above());
        } else return this.aiActivityType() == PrehistoricEntityTypeAI.Activity.BOTH;
    }

    public boolean canWakeUp() {
        if (this.aiActivityType() == PrehistoricEntityTypeAI.Activity.DIURINAL) {
            return this.isDaytime();
        } else if (this.aiActivityType() == PrehistoricEntityTypeAI.Activity.NOCTURNAL) {
            return !this.isDaytime() || this.level.canSeeSky(this.blockPosition().above());
        } else {
            return this.ticksSlept > 4000;
        }
    }

    public boolean isDaytime() {
        return this.level.isDay();
    }

    protected void doMoodCheck() {
        int overallMoodAddition = 0;
        if (this.arePlantsNearby(16)) {
            overallMoodAddition += 50;
        } else {
            overallMoodAddition -= 50;
        }
        if (!this.isThereNearbyTypes()) {
            overallMoodAddition += 50;
        } else {
            overallMoodAddition -= 50;
        }
        this.setMood(this.getMood() + overallMoodAddition);
    }

    /*
        How many dinosaurs of this type can exist in the same small, enclosed space.
     */
    public int getMaxPopulation() {
        return nearByMobsAllowed;
    }

    public boolean wantsToSleep() {
        if (!level.isClientSide &&
                this.aiActivityType() == PrehistoricEntityTypeAI.Activity.BOTH &&
                this.ticksSlept > 8000
        ) {
            return false;
        }
        return !level.isClientSide &&
                this.getTarget() == null &&
                this.getLastHurtByMob() == null &&
                !this.isInWater() &&
                !this.isVehicle() &&
                !this.isActuallyWeak() &&
                this.canSleep() &&
                canSleepWhileHunting() &&
                // (this.getAnimation() == NO_ANIMATION || this.getAnimation() == SPEAK_ANIMATION) &&
                this.getOrderType() != OrderType.FOLLOW;
    }

    private boolean canSleepWhileHunting() {
        return this.getTarget() == null || this.getTarget() instanceof EntityToyBase;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public Player getRidingPlayer() {
        if (this.getControllingPassenger() instanceof Player) {
            return (Player) getControllingPassenger();
        } else {
            return null;
        }
    }

    public void setRidingPlayer(Player player) {
        player.yBodyRot = this.yBodyRot;
        player.setXRot(this.getXRot());
        player.startRiding(this);
    }

    @Override
    public void travel(Vec3 movement) {
        if ((this.isOrderedToSit() || this.isImmobile()) && this.isVehicle()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(movement);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player player && this.getTarget() != passenger) {
                if (this.isTame() && this.isOwnedBy(player)) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isSkeleton()) {
            this.setDeltaMovement(Vec3.ZERO);
        }
        if ((this.getTarget() != null) && this.isSleeping()) {
            this.setSleeping(false);
        }
        if (this.getOwner() != null && this.getOwnerDisplayName().equals("")) {
            this.setOwnerDisplayName(this.getOwner().getDisplayName().toString());
        }
        if (this.getHunger() > this.getMaxHunger()) {
            this.setHunger(this.getMaxHunger());
        }
        if (this.getMood() > 100) {
            this.setMood(100);
        }
        if (this.getMood() < -100) {
            this.setMood(-100);
        }
        if (this.isDeadlyHungry() && this.getMood() > -50) {
            this.setMood(-50);
        }
        if (this.ticksTillPlay > 0) {
            this.ticksTillPlay--;
        }
        if (this.ticksTillMate > 0) {
            this.ticksTillMate--;
        }
        if (this.getRidingPlayer() != null) {
            this.maxUpStep = 1;
        }
        if (Fossil.CONFIG_OPTIONS.dinosaurBreeding &&
                !level.isClientSide &&
                ticksTillMate == 0 &&
                this.gender == Gender.MALE &&
                this.getMood() > 50
        ) {
            float cramDist = 30;
            AABB area = new AABB(this.getX() - cramDist,
                    this.getY() - cramDist / 2,
                    this.getZ() - cramDist,

                    this.getX() + cramDist,
                    this.getY() + cramDist,
                    this.getZ() + cramDist);
            List<? extends Prehistoric> crammedList = level.getEntitiesOfClass(this.getClass(), area);
            if (crammedList.size() > this.getMaxPopulation()) {
                this.ticksTillMate = this.random.nextInt(6000) + 6000;
            } else {
                this.mate();
            }
        }
        if (Fossil.CONFIG_OPTIONS.healingDinos && !this.level.isClientSide) {
            if (this.random.nextInt(500) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }
        }
        if (this.moodCheckCooldown-- <= 0) {
            this.doMoodCheck();
            this.moodCheckCooldown = 3000 + this.getRandom().nextInt(5000);
        }

        if (this.isSleeping()) {
            if ((this.getTarget() != null && this.getTarget().isAlive()) || (this.getLastHurtByMob() != null && this.getLastHurtByMob().isAlive())) {
                this.setSleeping(false);
            }
        }
        if (this.isOrderedToSit()) {
            ticksSat++;
        }
        if (!level.isClientSide) {
            if (this.isSleeping()) {
                ticksSlept++;
            } else {
                ticksSlept = 0;
            }
        }
        if (!level.isClientSide &&
                !this.isInWater() &&
                this.isVehicle() &&
                !this.isOrderedToSit() &&
                this.getRandom().nextInt(1000) == 1 &&
                !this.isPassenger() &&
                // (this.getAnimation() == NO_ANIMATION || this.getAnimation() == SPEAK_ANIMATION) &&
                !this.isSleeping()) {
            this.setOrderedToSit(true);
            ticksSat = 0;
        }

        if (!level.isClientSide &&
                !this.isInWater() &&
                (this.isOrderedToSit() && ticksSat > 100 && this.getRandom().nextInt(100) == 1 || this.getTarget() != null) &&
                !this.isSleeping()) {
            this.setOrderedToSit(false);
            ticksSat = 0;
        }
        if (cathermalSleepCooldown > 0) {
            cathermalSleepCooldown--;
        }
        if (!this.level.isClientSide && this.wantsToSleep()) {
            if (this.aiActivityType() == PrehistoricEntityTypeAI.Activity.BOTH) {
                if (cathermalSleepCooldown == 0) {
                    if (this.getRandom().nextInt(1200) == 1 && !this.isSleeping()) {
                        this.setOrderedToSit(false);
                        this.setSleeping(true);
                    }
                }
            } else if (this.aiActivityType() != PrehistoricEntityTypeAI.Activity.NOSLEEP) {
                if (this.getRandom().nextInt(200) == 1 && !this.isSleeping()) {
                    this.setOrderedToSit(false);
                    this.setSleeping(true);
                }
            }
        }
        if (!this.level.isClientSide && (!this.wantsToSleep() || !this.canSleep() || canWakeUp())) {
            this.setOrderedToSit(false);
            this.setSleeping(false);
        }

        if (this.currentOrder == OrderType.STAY && !this.isOrderedToSit() && !this.isActuallyWeak()) {
            this.setOrderedToSit(true);
            this.setSleeping(false);
        }
        if (breaksBlocks && this.getMood() < 0) {
            this.breakBlock(5);
        }
        if (this.getTarget() != null &&
                this.getTarget() instanceof EntityToyBase &&
                (isPreyBlocked(this.getTarget()) || this.ticksTillPlay > 0)) {
            this.setTarget(null);
        }
        if (isFleeing()) {
            fleeTicks++;
            if (fleeTicks > getFleeingCooldown()) {
                this.setFleeing(false);
                fleeTicks = 0;
            }
        }

        //don't use the vanilla system
        if (this.age < 0) {
            this.age = 0;
        }
        refreshDimensions();
        if (!this.isSkeleton()) {
            if (!this.isAgingDisabled()) {
                this.setAgeinTicks(this.getAgeInTicks() + 1);
                if (this.getAgeInTicks() % 24000 == 0) {
                    this.updateAbilities();
                    this.grow(0);
                }
            }
            if (this.tickCount % 1200 == 0 && this.getHunger() > 0 && Fossil.CONFIG_OPTIONS.starvingDinos) {
                this.setHunger(this.getHunger() - 1);
            }
            if (this.getHealth() > this.getMaxHealth() / 2 && this.getHunger() == 0 && this.tickCount % 40 == 0) {
                this.hurt(DamageSource.STARVE, 1);
            }
        }
        boolean sitting = isOrderedToSit();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
            if (sleepProgress != 0) {
                sleepProgress = 0F;
            }
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
            if (sleepProgress != 0) {
                sleepProgress = 0F;
            }
        }
        boolean sleeping = isSleeping();
        if (sleeping && sleepProgress < 20.0F) {
            sleepProgress += 0.5F;
            if (sitProgress != 0) {
                sitProgress = 0F;
            }
        } else if (!sleeping && sleepProgress > 0.0F) {
            sleepProgress -= 0.5F;
            if (sitProgress != 0) {
                sitProgress = 0F;
            }
        }

        boolean climbing = this.aiClimbType() == PrehistoricEntityTypeAI.Climbing.ARTHROPOD &&
                this.isBesideClimbableBlock() &&
                !this.onGround;

        if (climbing && climbProgress < 20.0F) {
            climbProgress += 2F;
            if (sitProgress != 0) {
                sitProgress = 0F;
            }
        } else if (!climbing && climbProgress > 0.0F) {
            climbProgress -= 2F;
            if (sitProgress != 0) {
                sitProgress = 0F;
            }
        }
        boolean weak = this.isActuallyWeak();
        if (weak && weakProgress < 20.0F) {
            weakProgress += 0.5F;
            sitProgress = 0F;
            sleepProgress = 0F;
        } else if (!weak && weakProgress > 0.0F) {
            weakProgress -= 0.5F;
            sitProgress = 0F;
            sleepProgress = 0F;
        }
        if (!this.level.isClientSide) {
            if (this.aiClimbType() == PrehistoricEntityTypeAI.Climbing.ARTHROPOD &&
                    !this.wantsToSleep() &&
                    !this.isSleeping() &&
                    ticksClimbing >= 0 && ticksClimbing < 100) {
                this.setBesideClimbableBlock(this.horizontalCollision);
            } else {
                this.setBesideClimbableBlock(false);
                if (ticksClimbing >= 100) {
                    ticksClimbing = -900;
                }
            }
            if (this.onClimbable() || ticksClimbing < 0) {
                ticksClimbing++;
                if (level.getBlockState(this.blockPosition().above()).getMaterial().isSolid()) {
                    ticksClimbing = 200;
                }
            }
        }
        // AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private boolean isAboveGround() {
        BlockPos blockPos = blockPosition();
        while (level.getBlockState(blockPos).isAir() && blockPos.getY() > 1) {
            blockPos = blockPos.below();
        }
        return this.getBoundingBox().minY > blockPos.getY();
    }

    @Override
    public abstract PrehistoricEntityTypeAI.Activity aiActivityType();

    @Override
    public abstract PrehistoricEntityTypeAI.Attacking aiAttackType();

    @Override
    public abstract PrehistoricEntityTypeAI.Climbing aiClimbType();

    @Override
    public abstract PrehistoricEntityTypeAI.Following aiFollowType();

    @Override
    public abstract PrehistoricEntityTypeAI.Jumping aiJumpType();

    @Override
    public abstract PrehistoricEntityTypeAI.Response aiResponseType();

    @Override
    public abstract PrehistoricEntityTypeAI.Stalking aiStalkType();

    @Override
    public abstract PrehistoricEntityTypeAI.Taming aiTameType();

    @Override
    public abstract PrehistoricEntityTypeAI.Untaming aiUntameType();

    @Override
    public abstract PrehistoricEntityTypeAI.Moving aiMovingType();

    @Override
    public abstract PrehistoricEntityTypeAI.WaterAbility aiWaterAbilityType();

    public boolean doesFlock() {
        return false;
    }

    public float getAgeScale() {
        float step = (this.maxSize - this.minSize) / ((this.adultAge * 24000) + 1);
        if (this.getAgeInTicks() > this.adultAge * 24000) {
            return this.minSize + ((step) * this.adultAge * 24000);
        }
        return this.minSize + ((step * this.getAgeInTicks()));
    }

    @Override
    protected int getExperienceReward(Player player) {
        float base = 6 * this.getActualWidth() * (this.diet == Diet.HERBIVORE ? 1.0F : 2.0F)
                * (this.aiTameType() == PrehistoricEntityTypeAI.Taming.GEM ? 1.0F : 2.0F)
                * (this.aiAttackType() == PrehistoricEntityTypeAI.Attacking.BASIC ? 1.0F : 1.25F);
        return Mth.floor((float) Math.min(this.adultAge, this.getAgeInDays()) * base);
    }

    public void updateAbilities() {
        double healthStep = (maxHealth - baseHealth) / (this.adultAge);
        double attackStep = (maxDamage - baseDamage) / (this.adultAge);
        double speedStep = (maxSpeed - baseSpeed) / (this.adultAge);
        double armorStep = (maxArmor - baseArmor) / (this.adultAge);
        if (this.getAgeInDays() <= this.adultAge) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.round(baseHealth + (healthStep * this.getAgeInDays())));
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.round(baseDamage + (attackStep * this.getAgeInDays())));
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed + (speedStep * this.getAgeInDays()));
            this.getAttribute(Attributes.ARMOR).setBaseValue(baseArmor + (armorStep * this.getAgeInDays()));
            if (this.developsResistance) {
                if (this.isTeen()) {
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
                } else if (this.isAdult()) {
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
                } else {
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
                }
            }
        }
        this.heal((float) healthStep);
    }

    public void breakBlock(float maxHardness) {
        if (!Fossil.CONFIG_OPTIONS.dinoBlockBreaking) return;
        if (isSkeleton()) return;
        if (!this.isAdult()) return;
        if (!this.isHungry()) return;
        // if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) return;
        // how do we respect other handlers canceling it?

        for (int a = (int) Math.round(this.getBoundingBox().minX) - 1; a <= (int) Math.round(this.getBoundingBox().maxX) + 1; a++) {
            for (int b = (int) Math.round(this.getBoundingBox().minY) + 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 2) && (b <= 127); b++) {
                for (int c = (int) Math.round(this.getBoundingBox().minZ) - 1; c <= (int) Math.round(this.getBoundingBox().maxZ) + 1; c++) {
                    BlockPos pos = new BlockPos(a, b, c);
                    if (level.getBlockState(pos).isAir()) continue;
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof BushBlock) continue;
                    if (!state.getFluidState().isEmpty()) continue;
                    if (state.getDestroySpeed(level, pos) >= maxHardness) continue;
                    if (!canBreak(state.getBlock())) continue;
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1F, 0.6F));
                    if (!level.isClientSide) {
                        level.destroyBlock(new BlockPos(a, b, c), true);
                    }
                }
            }
        }
    }


    public Entity createChild() {
        if (level.isClientSide) return null;
        Entity baby = null;
        if (this instanceof Mammal mammal) {
            baby = mammal.createChild((ServerLevel) level);
        }
        if (this instanceof FlyingAnimal) {
            baby = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), new ItemStack(cultivatedEggItem));
        }
        if (this instanceof IDinosaur) {
            baby = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), new ItemStack(this.cultivatedEggItem));
            /*
            For now disable this behavior as egg model does not exist yet.
            if (Fossil.CONFIG_OPTIONS.eggsLikeChickens || this.isVivariousAquatic()) {
            } else {
                baby = new DinosaurEgg(this.level, (EntityType<? extends Prehistoric>) this.getType(), eggScale);
            }
            */
        }
        return baby;
    }

    public boolean isVivariousAquatic() {
        return false;
    }

    public boolean isAdult() {
        return this.getAgeInDays() >= adultAge;
    }

    public boolean isTeen() {
        return this.getAgeInDays() >= teenAge && this.getAgeInDays() < adultAge;
    }

    @Override
    public boolean isBaby() {
        return this.getAgeInDays() < teenAge && !this.isSkeleton();
    }

    public abstract int getMaxHunger();

    public boolean isSkeleton() {
        return this.entityData.get(MODELIZED);
    }

    public void setSkeleton(boolean skeleton) {
        this.entityData.set(MODELIZED, skeleton);
    }

    public int getAgeInDays() {
        return this.entityData.get(AGETICK) / 24000;
    }

    public void setAgeInDays(int days) {
        this.entityData.set(AGETICK, days * 24000);
    }

    public int getAgeInTicks() {
        return this.entityData.get(AGETICK);
    }

    public void setAgeinTicks(int ticks) {
        refreshTexturePath();
        this.entityData.set(AGETICK, ticks);
    }

    public int getHunger() {
        return this.entityData.get(HUNGER);
    }

    public void setHunger(int hunger) {
        if (this.getHunger() > this.getMaxHunger()) {
            this.entityData.set(HUNGER, this.getMaxHunger());
        } else {
            this.entityData.set(HUNGER, hunger);
        }
    }

    public boolean isAgingDisabled() {
        return this.entityData.get(AGINGDISABLED);
    }

    public void setAgingDisabled(boolean isAgingDisabled) {
        this.entityData.set(AGINGDISABLED, isAgingDisabled);
    }

    public ItemStack getItemInMouth() {
        return this.entityData.get(HOLDING_IN_MOUTH);
    }

    public void setItemInMouth(@NotNull ItemStack stack) {
        this.entityData.set(HOLDING_IN_MOUTH, stack);
    }

    public boolean increaseHunger(int hunger) {
        if (this.getHunger() >= this.getMaxHunger()) {
            return false;
        }
        this.setHunger(this.getHunger() + hunger);
        if (this.getHunger() > this.getMaxHunger()) {
            this.setHunger(this.getMaxHunger());
        }
        this.level.playSound(null,
                this.blockPosition(),
                SoundEvents.GENERIC_EAT,
                SoundSource.NEUTRAL,
                this.getSoundVolume(),
                this.getVoicePitch()
        );
        return true;
    }


    public boolean isHungry() {
        return this.getHunger() < this.getMaxHunger() * 0.75F;
    }

    public boolean isDeadlyHungry() {
        return this.getHunger() < this.getMaxHunger() * 0.25F;
    }

    @Override
    public boolean onClimbable() {
        if (this.aiMovingType() == PrehistoricEntityTypeAI.Moving.AQUATIC ||
                this.aiMovingType() == PrehistoricEntityTypeAI.Moving.SEMIAQUATIC) {
            return false;
        } else {
            return this.aiClimbType() == PrehistoricEntityTypeAI.Climbing.ARTHROPOD &&
                    this.isBesideClimbableBlock() && !this.isImmobile();
        }
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, angry);
    }

    public int getSubSpecies() {
        return this.entityData.get(SUBSPECIES);
    }

    public void setSubSpecies(int subspecies) {
        this.entityData.set(SUBSPECIES, subspecies);
    }

    public int getMood() {
        return Mth.clamp(this.entityData.get(MOOD), -100, 100);
    }

    public void setMood(int mood) {
        this.entityData.set(MOOD, Mth.clamp(mood, -100, 100));
    }

    public PrehistoricMoodType getMoodFace() {
        if (this.getMood() == 100) {
            return PrehistoricMoodType.HAPPY;
        } else if (this.getMood() >= 50) {
            return PrehistoricMoodType.CONTENT;
        } else if (this.getMood() == -100) {
            return PrehistoricMoodType.ANGRY;
        } else if (this.getMood() <= -50) {
            return PrehistoricMoodType.SAD;
        } else {
            return PrehistoricMoodType.CALM;
        }
    }

    public int getScaledMood() {
        return (int) (71 * -(this.getMood() * 0.01));
    }

    @Override
    public boolean rideableUnderWater() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL) {
            return false;
        }
        if (amount > 0 && this.isSkeleton()) {
            this.level.playSound(null,
                    this.blockPosition(),
                    SoundEvents.SKELETON_HURT,
                    SoundSource.NEUTRAL,
                    this.getSoundVolume(),
                    this.getVoicePitch()
            );
            if (!level.isClientSide && !droppedBiofossil) {
                if (this.timePeriod == TimePeriod.CENOZOIC) {
                    this.spawnAtLocation(ModItems.TAR_FOSSIL.get(), 1);
                } else {
                    this.spawnAtLocation(ModItems.BIO_FOSSIL.get(), 1);
                }
                this.spawnAtLocation(new ItemStack(Items.BONE, Math.min(this.getAgeInDays(), this.adultAge)), 1);
                droppedBiofossil = true;
            }
            this.dead = true;
            return true;
        }
        if (this.getLastHurtByMob() instanceof Player) {
            if (this.getOwner() == this.getLastHurtByMob()) {
                this.setTame(false);
                this.setMood(this.getMood() - 15);
            }
        }

        if (amount > 0) {
            this.setOrderedToSit(false);
            this.setSleeping(false);
        }
        if (source.getEntity() != null) {
            this.setMood(this.getMood() - 5);
        }
        /*if (this.getHurtSound(DamageSource.GENERIC) != null && amount >= 1 && dmg != DamageSource.IN_WALL) {
            if (this.getAnimation() != null) {
                if (this.getAnimation() == NO_ANIMATION && level.isClientSide) {
                    this.setAnimation(SPEAK_ANIMATION);
                }
            }
        }*/
        return super.hurt(source, amount);
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (this.aiClimbType() == PrehistoricEntityTypeAI.Climbing.ARTHROPOD ||
                this.aiMovingType() == PrehistoricEntityTypeAI.Moving.WALKANDGLIDE ||
                this.aiMovingType() == PrehistoricEntityTypeAI.Moving.FLIGHT
        ) {
            return false;
        } else {
            return super.causeFallDamage(distance, damageMultiplier, source);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isSkeleton()) {
            if (itemstack.isEmpty()) {
                if (player.isShiftKeyDown()) {
                    this.nudgeEntity(player);
                } else {
                    double d0 = player.getX() - this.getX();
                    double d2 = player.getZ() - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                    this.yHeadRot = f;
                    this.yBodyRot = f;
                }
                return InteractionResult.SUCCESS;
            } else {
                if (itemstack.getItem() == Items.BONE && this.getAgeInDays() < this.adultAge) {
                    this.level.playSound(null, this.blockPosition(), SoundEvents.SKELETON_AMBIENT, SoundSource.NEUTRAL, 0.8F, 1);
                    this.setAgeInDays(this.getAgeInDays() + 1);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        } else {

            if (!itemstack.isEmpty()) {
                // TODO Add GEM items
                /*if ((this.aiTameType() == PrehistoricEntityTypeAI.Taming.GEM && itemstack.getItem() == ModItems.SCARAB_GEM) || (this.aiTameType() == PrehistoricEntityTypeAI.Taming.BLUEGEM && itemstack.getItem() == ModItems.AQUATIC_SCARAB_GEM)) {
                    if (!this.isTame() && !this.isOwnedBy(player) && this.isActuallyWeak()) {
                        this.triggerTamingAcheivement(player);
                        this.heal(200);
                        this.setMood(100);
                        this.increaseHunger(500);
                        this.getNavigation().stop();
                        this.setTarget(null);
                        this.setLastHurtByMob(null);
                        this.setTame(true);
                        this.setOwnerUUID(player.getUUID());
                        this.level.setEntityState(this, (byte) 35); // also somehow port this
                        itemstack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }*/

                if (itemstack.getItem() == ModItems.CHICKEN_ESSENCE.get() && this.aiTameType() != PrehistoricEntityTypeAI.Taming.GEM && this.aiTameType() != PrehistoricEntityTypeAI.Taming.BLUEGEM && !player.level.isClientSide) {
                    if (this.getAgeInDays() < this.adultAge && this.getHunger() > 0) {
                        if (this.getHunger() > 0) {
                            itemstack.shrink(1);
                            if (!player.isCreative()) {
                                player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
                            }
                            //Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), Item.getIdFromItem(ModItems.CHICKEN_ESSENCE)));
                            this.grow(1);
                            this.setHunger(1 + (new Random()).nextInt(this.getHunger()));
                            return InteractionResult.SUCCESS;
                        }
                    }
                    if (!this.level.isClientSide) {
                        player.displayClientMessage(new TranslatableComponent("prehistoric.essencefail"), true);
                    }
                    return InteractionResult.SUCCESS;
                }
                /*if (itemstack.getItem() == ModItems.STUNTED_ESSENCE && !isAgingDisabled()) { // TODO Add STUNTED_ESSENCE
                    this.setHunger(this.getHunger() + 20);
                    this.heal(this.getMaxHealth());
                    this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getVoicePitch());
                    spawnItemCrackParticles(itemstack.getItem());
                    spawnItemCrackParticles(itemstack.getItem());
                    spawnItemCrackParticles(Items.POISONOUS_POTATO);
                    spawnItemCrackParticles(Items.POISONOUS_POTATO);
                    spawnItemCrackParticles(Items.EGG);
                    this.setAgingDisabled(true);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }*/

                if (FoodMappings.getItemFoodAmount(itemstack, this.diet) != 0) {
                    if (!player.level.isClientSide) {
                        if (this.getHunger() < this.getMaxHunger() || this.getHealth() < this.getMaxHealth() && Fossil.CONFIG_OPTIONS.healingDinos || !this.isTame() && this.aiTameType() == PrehistoricEntityTypeAI.Taming.FEEDING) {
                            this.setHunger(this.getHunger() + FoodMappings.getItemFoodAmount(itemstack, this.diet));
                            if (!level.isClientSide) {
                                this.eatItem(itemstack);
                            }
                            if (Fossil.CONFIG_OPTIONS.healingDinos) {
                                this.heal(3);
                            }
                            if (this.getHunger() >= this.getMaxHunger()) {
                                if (this.isTame()) {
                                }
                            }
                            itemstack.shrink(1);
                            if (this.aiTameType() == PrehistoricEntityTypeAI.Taming.FEEDING) {
                                if (!this.isTame() && this.isTameable() && (new Random()).nextInt(10) == 1) {
                                    this.setTame(true);
                                    this.setOwnerUUID(player.getUUID());
                                    this.level.broadcastEntityEvent(this, (byte) 35);
                                }
                            }

                            return InteractionResult.SUCCESS;
                        } else {
                            return InteractionResult.PASS;
                        }
                    }

                    return InteractionResult.PASS;
                } else {
                    if (itemstack.getItem() == Items.LEAD && this.isTame()) {
                        if (this.isOwnedBy(player)) {
                            this.setLeashedTo(player, true);
                            itemstack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    /*if (level.isClientSide && itemstack.getItem() == ModItems.DINOPEDIA) { // TODO Add DIOPEDIA
                        this.setPedia();
                        //  player.openGui(Revival.INSTANCE, 6, this.level, (int) this.getX(), (int) this.getY(), (int) this.getZ());
                        return InteractionResult.SUCCESS;
                    }*/

                    /*if (itemstack.getItem() == ModItems.WHIP && this.aiTameType() != PrehistoricEntityTypeAI.Taming.NONE && this.isAdult()) { // TODO Add WHIP
                        if (this.isTame() && this.isOwnedBy(player) && this.canBeRidden()) {
                            if (this.getRidingPlayer() == null) {
                                if (!this.level.isClientSide) {
                                    //   Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), FABlockRegistry.VOLCANIC_ROCK));
                                    setRidingPlayer(player);
                                }
                                this.setOrder(OrderType.WANDER);
                                this.setOrderedToSit(false);
                                this.setSleeping(false);
                            } else if (this.getRidingPlayer() == player) {
                                this.setSprinting(true);
                                if (!this.level.isClientSide) {
                                    //    Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), FABlockRegistry.VOLCANIC_ROCK));
                                }
                                this.setMood(this.getMood() - 5);
                            }
                        } else if (!this.isTame() && this.aiTameType() != PrehistoricEntityTypeAI.Taming.BLUEGEM && this.aiTameType() != PrehistoricEntityTypeAI.Taming.GEM) {
                            this.setMood(this.getMood() - 5);
                            //  Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), FABlockRegistry.VOLCANIC_ROCK));
                            if (getRandom().nextInt(5) == 0) {
                                player.displayClientMessage(new TranslatableComponent("prehistoric.autotame", this.getName().getString()), true);
                                this.setMood(this.getMood() - 25);
                                this.setTame(true);
                                //    Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), Item.getIdFromItem(Items.GOLD_INGOT)));
                                this.setOwnerUUID(player.getUUID());
                            }
                        }
                        this.setOrderedToSit(false);
                        // this.setOrder(OrderType.WANDER);

                        // this.currentOrder = OrderType.FreeMove;
                        // setRidingPlayer(player);
                    }*/
                    if (this.getOrderItem() != null && itemstack.getItem() == this.getOrderItem() && this.isTame() && this.isOwnedBy(player) && !player.isPassenger()) {
                        if (!this.level.isClientSide) {
                            this.jumping = false;
                            this.getNavigation().stop();
                            this.currentOrder = OrderType.values()[(this.currentOrder.ordinal() + 1) % 3];
                            this.sendOrderMessage(this.currentOrder);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    protected boolean isTameable() {
        return true;
    }

    public abstract Item getOrderItem();

    private void triggerTamingAcheivement(Player player) {
        // player.triggerAchievement(FossilAchievementHandler.theKing);

    }

    public void grow(int ageInDays) {
        if (this.isAgingDisabled()) {
            return;
        }
        this.setAgeInDays(this.getAgeInDays() + ageInDays);
        for (int i = 0; i < this.getAgeScale() * 4; i++) {
            double motionX = getRandom().nextGaussian() * 0.07D;
            double motionY = getRandom().nextGaussian() * 0.07D;
            double motionZ = getRandom().nextGaussian() * 0.07D;
            double minX = this.getBoundingBox().minX;
            double minY = this.getBoundingBox().minY;
            double minZ = this.getBoundingBox().minZ;


            float x = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxX - minX) + minX);
            float y = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxY - minY) + minY);
            float z = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxZ - minZ) + minZ);
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, motionX, motionY, motionZ);

        }
        this.updateAbilities();
    }

    public boolean isWeak() {
        return (this.getHealth() < 8) && (this.getAgeInDays() >= this.adultAge) && !this.isTame();
    }

    /*protected void setPedia() {
        Revival.PROXY.setPediaObject(this);
        // Currently there's only empty implementation for `ServerProxy#setPediaObject`
    }*/

    private void sendOrderMessage(OrderType var1) {
        String s = "dino.order." + var1.name().toLowerCase();
        if (this.getOwner() instanceof Player player) {
            player.displayClientMessage(new TranslatableComponent(s, this.getName()), true);
        }
    }

    public void nudgeEntity(Player player) {
        this.teleportTo(this.getX() + (player.getX() - this.getX()) * 0.01F, this.getY(), this.getZ() + (player.getZ() - this.getZ()) * 0.01F);
    }

    public ArrayList<Class<? extends Entity>> preyList() {
        return new ArrayList<>();
    }

    public ArrayList<Class<? extends Entity>> preyBlacklist() {
        return new ArrayList<>();
    }

    public void playerRoar(Player player) {
    }

    public void playerAttack(Player player) {
    }

    public void playerJump(Player player) {
    }

    public void playerFlyUp(Player player) {
    }

    public void playerFlyDown(Player player) {
    }

    public void refreshTexturePath() {
        String name = getType().arch$registryName().getPath();
        StringBuilder builder = new StringBuilder();
        builder.append("textures/entity/");
        builder.append(name);
        builder.append("/");
        builder.append(name);
        if (this.isSkeleton()) {
            builder.append("_skeleton.png");
        } else {
            if (isBaby()) builder.append("_baby");
            if (isTeen()) builder.append("_teen");
            if (isAdult()) {
                if (gender == Gender.MALE) {
                    builder.append("_male");
                } else {
                    builder.append("_female");
                }
            }
            if (isSleeping()) builder.append("_sleeping");
            builder.append(".png");
        }
        String path = builder.toString();
        textureLocation = new ResourceLocation(Fossil.MOD_ID, path);
    }

    public boolean isActuallyWeak() {
        return (this.aiTameType() == PrehistoricEntityTypeAI.Taming.BLUEGEM || this.aiTameType() == PrehistoricEntityTypeAI.Taming.GEM) && this.isWeak();
    }

    public int getTailSegments() {
        return 3;
    }

    @Override
    public float getSpeed() {
        return 0.4F;
    }

    public float getMaleSize() {
        return 1.0F;
    }

    public String getOverlayTexture() {
        return "fossil:textures/blank.png";
    }

    /*@Override
    public int getAnimationTick() {
        return animTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation == null ? NO_ANIMATION : currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{SPEAK_ANIMATION, ATTACK_ANIMATION};
    }*/

    @Override
    public void playAmbientSound() {
        if (!this.isSleeping() && !this.isSkeleton()) {
            super.playAmbientSound();
            /*if (this.getAnimation() != null) {
                if (this.getAnimation() == NO_ANIMATION && !level.isClientSide) {
                    this.setAnimation(SPEAK_ANIMATION);
                }
            }*/
        }
    }

    public void knockbackEntity(Entity knockBackMob, float knockbackStrength, float knockbackStrengthUp) {
        if (!(knockBackMob instanceof EntityToyBase) && knockBackMob instanceof LivingEntity) {
            double resistance = ((LivingEntity) knockBackMob).getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
            double reversed = 1 - resistance;
            knockBackMob.setDeltaMovement(knockBackMob.getDeltaMovement().add(0, 0.4000000059604645D * reversed + 0.1D, 0));
            if (resistance < 1) {
                knockBackMob(knockBackMob, 0.25D, 0.2D, 0.25D);
            }
        }
    }

    public boolean canDinoHunt(LivingEntity target, boolean hunger) {
        if (target instanceof EntityToyBase) {
            return true;
        }
        boolean isAnotherDino = target instanceof Prehistoric;
        boolean b = true;
        if (target != null) {
            b = true;// FoodHelper.getMobFoodPoints((LivingEntity) target, this.diet) > 0;
        }
        if (this.diet != Diet.HERBIVORE && this.diet != Diet.NONE && b && canAttack(target)) {
            if (isAnotherDino ? this.getActualWidth() * getTargetScale() >= ((Prehistoric) target).getActualWidth() : this.getActualWidth() * getTargetScale() >= target.getBbWidth()) {
                if (hunger) {
                    return isHungry();
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public float getTargetScale() {
        return 1.0F;
    }

    public boolean isMad() {
        return this.getMoodFace() == PrehistoricMoodType.SAD;
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return otherAnimal != this &&
                otherAnimal.getClass() == this.getClass() &&
                this.isAdult() &&
                this.getMood() > 50 &&
                this.ticksTillMate == 0 &&
                ((Prehistoric) otherAnimal).gender != this.gender;
    }

    public void mate() {
        if (this.gender != Gender.MALE) return;

        double inflateAmount = 64;
        List<? extends Prehistoric> listOfFemales = level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(inflateAmount, 4.0D, inflateAmount), entity -> {
            if (entity.getGender() != Gender.FEMALE) return false;
            if (entity.getClass() != this.getClass()) return false;
            if (!entity.isAdult()) return false;
            if (entity.ticksTillMate > 0) return false;
            return false;
        });
        if (!listOfFemales.isEmpty() && this.ticksTillMate == 0) {
            Prehistoric prehistoric = listOfFemales.get(0);
            if (prehistoric.ticksTillMate == 0) {
                this.getNavigation().moveTo(prehistoric, 1);
                double distance = this.getBbWidth() * 8.0F * this.getBbWidth() * 8.0F + prehistoric.getBbWidth();
                if (this.distanceToSqr(prehistoric.getX(), prehistoric.getBoundingBox().minY, prehistoric.getZ()) <= distance && prehistoric.onGround && this.onGround && this.isAdult() && prehistoric.isAdult()) {
                    prehistoric.procreate(this);
                    this.ticksTillMate = this.random.nextInt(6000) + 6000;
                    prehistoric.ticksTillMate = this.random.nextInt(12000) + 24000;
                }
            }
        }
    }

    @Override
    protected PathNavigation createNavigation(Level levelIn) {
        return this.aiClimbType() == PrehistoricEntityTypeAI.Climbing.ARTHROPOD ? new WallClimberNavigation(this, levelIn) : new DinosaurPathNavigator(this, levelIn);
    }


    public abstract boolean canBeRidden();

    @Override
    public boolean canBeControlledByRider() {
        return canBeRidden() && this.getControllingPassenger() instanceof LivingEntity && this.isOwnedBy((LivingEntity) this.getControllingPassenger());
    }

    public void procreate(Prehistoric mob) {
        for (int i = 0; i < 7; ++i) {
            double dd = this.random.nextGaussian() * 0.02D;
            double dd1 = this.random.nextGaussian() * 0.02D;
            double dd2 = this.random.nextGaussian() * 0.02D;
            //      Revival.PROXY.spawnPacketHeartParticles(this.level, (float) (this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth()), (float) (this.getY() + 0.5D + (this.random.nextFloat() * this.height)), (float) (this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth()), dd, dd1, dd2);
            //      Revival.PROXY.spawnPacketHeartParticles(mob.level, (float) (mob.posX + (mob.random.nextFloat() * mob.width * 2.0F) - mob.width), (float) (mob.posY + 0.5D + (mob.random.nextFloat() * mob.height)), (float) (mob.posZ + (mob.random.nextFloat() * mob.width * 2.0F) - mob.width), dd, dd1, dd2);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (this.random.nextInt(100) == 0 || calendar.get(2) + 1 == 4 && calendar.get(5) == 1) {
            //   this.playSound(FASoundRegistry.MUSIC_MATING, 1, 1);
        }
        if (!level.isClientSide) {
            Entity hatchling = this.createChild();
            this.setTarget(null);
            mob.setTarget(null);
            hatchling.moveTo(mob.getX(), mob.getY() + 1, mob.getZ(), mob.yBodyRot, 0);
            if (hatchling instanceof DinosaurEgg) {
                //      Revival.NETWORK_WRAPPER.sendToAll(new MessageUpdateEgg(hatchling.getEntityId(), this.dinoType.ordinal()));
            } else {
                if (hatchling instanceof Prehistoric) {
                    ((Prehistoric) hatchling).grow(1);
                    ((Prehistoric) hatchling).setHealth((float) this.baseHealth);
                }
            }
            this.level.addFreshEntity(hatchling);
        }
    }

    public boolean isThereNearbyTypes() {
        double d0 = 40;
        List<? extends Prehistoric> list = level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(d0, 4.0D, d0), Prehistoric::isAdult);
        list.remove(this);
        return list.size() > this.nearByMobsAllowed;

    }

    public void doFoodEffect(Item item) {
        this.level.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, this.getSoundVolume(), this.getVoicePitch());
        if (item != null) {
            if (item instanceof BlockItem) {
                spawnItemParticle(item, true);
            } else {
                spawnItemParticle(item, false);
            }
        }
    }

    public void doFoodEffect() {
        this.level.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, this.getSoundVolume(), this.getVoicePitch());
        switch (this.diet) {
            case HERBIVORE:
                spawnItemParticle(Items.WHEAT_SEEDS, false);
                break;
            case OMNIVORE:
                spawnItemParticle(Items.BREAD, false);
                break;
            case PISCIVORE:
                spawnItemParticle(Items.COD, false);
                break;
            default:
                spawnItemParticle(Items.BEEF, false);
                break;
        }
    }

    public void spawnItemCrackParticles(Item item) {
        for (int i = 0; i < 15; i++) {
            double motionX = getRandom().nextGaussian() * 0.07D;
            double motionY = getRandom().nextGaussian() * 0.07D;
            double motionZ = getRandom().nextGaussian() * 0.07D;
            float f = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
            float f1 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
            float f2 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
            if (level.isClientSide) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)), f, f1, f2, motionX, motionY, motionZ);
            }
        }
    }


    public void spawnItemParticle(Item item, boolean itemBlock) {
        if (!level.isClientSide) {
            double motionX = random.nextGaussian() * 0.07D;
            double motionY = random.nextGaussian() * 0.07D;
            double motionZ = random.nextGaussian() * 0.07D;
            float f = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
            float f1 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
            float f2 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
          /*  if (itemBlock && item instanceof ItemBlock) {
                Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(this.getEntityId(), Block.getIdFromBlock(((ItemBlock) item).getBlock())));
            } else {
                Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(this.getEntityId(), Item.getIdFromItem(item)));
            }
           */
        }
    }

    // This method uses Forge-specific API and needs porting. However, it's currently unused so I'm commenting it for now.
    /*public boolean isInWaterMaterial() {
        double d0 = this.getY();
        int i = Mth.floor(this.getX());
        int j = Mth.floor((float) Mth.floor(d0));
        int k = Mth.floor(this.getZ());
        BlockState blockState = this.level.getBlockState(new BlockPos(i, j, k));
        if (blockState.getMaterial() == Material.WATER) {
            double filled = 1.0f;
            if (blockState.getBlock() instanceof IFluidBlock) {
                filled = ((IFluidBlock) blockState.getBlock()).getFilledPercentage(level, new BlockPos(i, j, k));
            }
            if (filled < 0) {
                filled *= -1;
                return d0 > j + (1 - filled);
            } else {
                return d0 < j + filled;
            }
        } else {
            return false;
        }
    }*/

    public void eatItem(ItemStack stack) {
        if (stack != null) {
            if (FoodMappings.getItemFoodAmount(stack, diet) != 0) {
                this.setMood(this.getMood() + 5);
                doFoodEffect(stack.getItem());
                //Revival.NETWORK_WRAPPER.sendToAll(new MessageFoodParticles(getEntityId(), Item.getIdFromItem(stack.getItem())));
                this.setHunger(this.getHunger() + FoodMappings.getItemFoodAmount(stack, diet));
                stack.shrink(1);
            /*if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(SPEAK_ANIMATION);
            }*/
            }
        }
    }

    public String getTempermentString() {
        String s = null;
        if (this.aiResponseType() == PrehistoricEntityTypeAI.Response.AGRESSIVE || this.aiResponseType() == PrehistoricEntityTypeAI.Response.WATERAGRESSIVE) {
            s = "agressive";
        } else if (this.aiResponseType() == PrehistoricEntityTypeAI.Response.SCARED) {
            s = "scared";
        } else if (this.aiResponseType() == PrehistoricEntityTypeAI.Response.NONE || this.aiResponseType() == PrehistoricEntityTypeAI.Response.WATERCALM) {
            s = "none";
        } else if (this.aiResponseType() == PrehistoricEntityTypeAI.Response.TERITORIAL) {
            s = "territorial";
        }
        return "pedia.temperament." + s;
    }

    public boolean canRunFrom(Entity entity) {
        if (this.getBbWidth() <= entity.getBbWidth()) {
            if (entity instanceof Prehistoric) {
                Prehistoric mob = (Prehistoric) entity;
                return mob.diet != Diet.HERBIVORE;
            } else {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    return !this.isOwnedBy(player);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void positionRider(Entity passenger) {
        super.positionRider(passenger);
        if (this.hasPassenger(passenger)) {
            this.yBodyRot = passenger.getYRot();
        }
        if (this.getRidingPlayer() != null && this.isOwnedBy(this.getRidingPlayer()) && this.getTarget() != this.getRidingPlayer()) {
            yBodyRot = this.getRidingPlayer().yBodyRot;
            yHeadRot = this.getRidingPlayer().yBodyRot;
            float radius = ridingXZ * (0.7F * getAgeScale()) * -3;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double extraY = ridingY * (getAgeScale());
            float spinosaurusAddition = 0;
         /*    if (this instanceof EntitySpinosaurus) {
                spinosaurusAddition = -(((EntitySpinosaurus) this).swimProgress * 0.1F);
            }*/
            this.getRidingPlayer().teleportTo(this.getX() + extraX, this.getY() + extraY + spinosaurusAddition - 1.75F, this.getZ() + extraZ);
        }
       /* if (passenger instanceof EntityVelociraptor || passenger instanceof EntityDeinonychus) {
            double extraY = Math.min(ridingY * (getAgeScale()) - 1D, 0.5D);
            passenger.setPosition(this.getX(), this.getY() + extraY, this.getZ());
        }/
        //TODO
        */
    }

    private double getJumpStrength() {
        return 3D;
    }

    protected boolean isDinoJumping() {
        return horseJumping;
    }

    protected void setDinoJumping(boolean jump) {
        horseJumping = jump;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob AgeableMob) {
        if (AgeableMob instanceof Prehistoric) {
            Entity baby = this.createChild();
            Prehistoric prehistoric = (Prehistoric) baby;
            prehistoric.setAgeInDays(0);
            prehistoric.grow(0);
            prehistoric.updateAbilities();
            prehistoric.setNoAi(false);
            return ((Prehistoric) baby);
        }
        return null;
    }

    public boolean isAquatic() {
        return this.getMobType() == MobType.WATER;
    }

    public void onWhipRightClick() {

    }

    public boolean canReachPrey() {
        return this.getTarget() != null && getAttackBounds().intersects(this.getTarget().getBoundingBox()) && !isPreyBlocked(this.getTarget());
    }

    public boolean isPreyBlocked(Entity prey) {
        return this.hasLineOfSight(prey);
    }

    public boolean rayTraceFeeder(BlockPos position, boolean leaves) {
        return true;
    }

    private boolean isFeeder(BlockPos pos, boolean leaves) {
        if (leaves) {
            BlockState state = level.getBlockState(pos);
            return FoodMappings.getBlockFoodAmount(state.getBlock(), this.diet) > 0;
        } else {
            BlockState state = level.getBlockState(pos);
            BlockEntity entity = this.level.getBlockEntity(pos);
            return false;//  return entity instanceof TileEntityFeeder;
        }
    }

    public float getDeathRotation() {
        return 90.0F;
    }

    protected float getSoundVolume() {
        return this.isBaby() ? super.getSoundVolume() * 0.75F : 1.0F;
    }

    protected void doAttackKnockback(float strength) {
        if (this.getTarget() != null) {
            if (this.getTarget().hasPassenger(this)) {
                this.getTarget().stopRiding();
            }
            knockbackEntity(this.getTarget(), strength, 0.1F);
            this.getTarget().hurtMarked = false;
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean wasEffective = super.doHurtTarget(target);
        if (!wasEffective) setFleeing(true); // Freak. Run!
        return wasEffective;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 45) {
            spawnItemParticle(Items.WHEAT_SEEDS);
            spawnItemParticle(Items.WHEAT_SEEDS);
            spawnItemParticle(Items.WHEAT_SEEDS);
        } else if (id == 46) {
            spawnItemParticle(Items.BREAD);
            spawnItemParticle(Items.BREAD);
            spawnItemParticle(Items.BREAD);
        } else if (id == 47) {
            spawnItemParticle(Items.BEEF);
            spawnItemParticle(Items.BEEF);
            spawnItemParticle(Items.BEEF);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public void spawnItemParticle(Item item) {
        if (this.level.isClientSide) return;
        double motionX = random.nextGaussian() * 0.07D;
        double motionY = random.nextGaussian() * 0.07D;
        double motionZ = random.nextGaussian() * 0.07D;
        double f = (float) (random.nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
        double f1 = (float) (random.nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
        double f2 = (float) (random.nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
        this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)), f, f1, f2, motionX, motionY, motionZ);
    }

    public float getMaxTurnDistancePerTick() {
        return Mth.clamp(90 - this.getActualWidth() * 20, 10, 90);
    }

    /*@OnlyIn(Dist.CLIENT)
    public void setJumpPower(int jumpPowerIn) {
        if (!this.isVehicle()) {
            if (jumpPowerIn < 0) {
                jumpPowerIn = 0;
            }

            if (jumpPowerIn >= 90) {
                this.jumpPower = 1.0F;
            } else {
                this.jumpPower = 0.4F + 0.4F * (float) jumpPowerIn / 90.0F;
            }
        }
    }*/

    @Override
    public boolean canJump() {
        return !this.isVehicle();
    }

    @Override
    public void handleStartJump(int i) {
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public void onPlayerJump(int jumpPower) {
    }

    public float getProximityToNextPathSkip() {
        return this.getBbWidth() > 0.75F ? this.getBbWidth() / 2.0F : 0.75F - this.getBbWidth() / 2.0F;
    }

    public boolean useSpecialAttack() {
        return false;
    }

    public boolean isFleeing() {
        return this.entityData.get(FLEEING);
    }

    public void setFleeing(boolean fleeing) {
        this.entityData.set(FLEEING, fleeing);
    }

    protected int getFleeingCooldown() {
        if (this.getLastHurtByMob() != null) {
            int i = (int) (Math.max(this.getLastHurtByMob().getBbWidth() / 2F, 1) * 95);
            int j = (int) (Math.min(this.getBbWidth() / 2F, 0.5D) * 50);
            return i - j;
        }
        return 100;
    }

    public void setGender(@NotNull Gender gender) {
        this.gender = gender;
        refreshTexturePath();
    }

    @NotNull
    public Gender getGender() {
        if (this.gender == null) throw new NullPointerException();
        return this.gender;
    }
}
