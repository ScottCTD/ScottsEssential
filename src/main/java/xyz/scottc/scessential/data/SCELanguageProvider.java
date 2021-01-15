package xyz.scottc.scessential.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xyz.scottc.scessential.utils.TextUtils;

public class SCELanguageProvider extends LanguageProvider {

    public SCELanguageProvider(DataGenerator gen, String modId, String locale) {
        super(gen, modId, locale);
    }

    @Override
    protected void addTranslations() {
        this.addCommandsTranslations();
        this.addServicesTranslations();
    }

    private void addCommandsTranslations() {
        // common
        this.add(getMessageTranslationKey("you"), "You");
        this.add(getMessageTranslationKey("ok"), "Okay");
        this.add(getMessageTranslationKey("clickToTeleport"), "Click to teleport!");
        // cooldown
        this.add(getMessageTranslationKey("inCoolDown"), "Teleport Cooldown: %f seconds.");
        // spawn
        this.add(getMessageTranslationKey("spawnSuccess"), "Successfully teleported to spawn!");
        // back
        this.add(getMessageTranslationKey("noBack"), "You don't have any teleport history more.");
        this.add(getMessageTranslationKey("backSuccess"), "Successfully returned to previous position!");
        // home
        this.add(getMessageTranslationKey("homeSuccess"), "Successfully teleported to home \"%s\"!");
        this.add(getMessageTranslationKey("noHome"), "You don't have any home currently.");
        this.add(getMessageTranslationKey("homeNotFound"), "Home \"%s\" not found!");
        this.add(getMessageTranslationKey("setHomeSuccess"), "Successfully set home \"%s\"!");
        this.add(getMessageTranslationKey("reachMaxHome"), "You cannot set more than %d homes!");
        this.add(getMessageTranslationKey("homeExist"), "Home \"%s\" already existed!");
        this.add(getMessageTranslationKey("delHomeSuccess"), "Successfully deleted home \"%s\"!");
        // home other
        this.add(getMessageTranslationKey("homeOtherNotFound"), "Player \"%s\"'s home \"%s\" not found!");
        this.add(getMessageTranslationKey("otherHomeSuccess"), "Successfully teleported to player \"%s\"'s home \"%s\"");
        this.add(getMessageTranslationKey("delOthersHomeSuccess"), "Successfully deleted player \"%s\"'s home \"%s\"!");
        this.add(getMessageTranslationKey("otherNoHome"), "Player \"%s\" doesn't have any home currently.");
        // TPA
        this.add(getMessageTranslationKey("accept"), "Accept");
        this.add(getMessageTranslationKey("Deny"), "Deny");
        this.add(getMessageTranslationKey("acceptHover"), "Click to accept this request!");
        this.add(getMessageTranslationKey("denyHover"), "Click to deny this request!");
        this.add(getMessageTranslationKey("cantTPASelf"), "You shouldn't send teleport request to yourself!");
        this.add(getMessageTranslationKey("requestSent"), "Request sent to player \"%s\"!");
        this.add(getMessageTranslationKey("tpaRequestMessage"), "Player \"%s\" want to teleport to your position.");
        this.add(getMessageTranslationKey("requestNotFound"), "Request expired!");
        this.add(getMessageTranslationKey("tpaSuccessSource"), "Successfully teleported to player \"%s\"!");
        this.add(getMessageTranslationKey("tpaSuccessTarget"), "Successfully teleported player \"%s\" to your position!");
        this.add(getMessageTranslationKey("tpaDenySource"), "Player \"%s\" denied your request.");
        this.add(getMessageTranslationKey("requestTimeout"), "Request related to player \"%s\" expired!");
        this.add(getMessageTranslationKey("tpaHereRequestMessage"), "Player \"%s\" wants you to teleport to his/her position.");
        // RTP
        this.add(getMessageTranslationKey("startRTP"), "Start finding safe landing site!");
        this.add(getMessageTranslationKey("rtpAttempts"), "Found safe landing site after %d attempt(s) and let's start teleporting!");
        this.add(getMessageTranslationKey("rtpSuccess"), "Safely and randomly teleported to x: %d y: %d z: %d!");
        this.add(getMessageTranslationKey("rtpFail"), "Failed to find a safe location. Please retry later!");
        // Warp
        this.add(getMessageTranslationKey("setWarpSuccess"), "Successfully set warp \"%s\"!");
        this.add(getMessageTranslationKey("warpNotFound"), "Warp \"%s\" not found!");
        this.add(getMessageTranslationKey("warpSuccess"), "Successfully teleported to warp \"%s\"!");
        this.add(getMessageTranslationKey("delWarpSuccess"), "Successfully deleted warp \"%s\"!");
        this.add(getMessageTranslationKey("noWarp"), "There isn't any warp available!");
        // Fly
        this.add(getMessageTranslationKey("cantSetFly"), "You cannot control creative player \"%s\"!");
        this.add(getMessageTranslationKey("cantFlyNow"), "Now, you're unable to fly.");
        this.add(getMessageTranslationKey("flyPermanentlySource"), "Player \"%s\" now can fly permanently!");
        this.add(getMessageTranslationKey("flyPermanentlyTarget"), "Now, you can fly permanently!");
        this.add(getMessageTranslationKey("flyTempTarget"), "Now, you can fly until %s!");
        this.add(getMessageTranslationKey("flyTempSource"), "Player \"%s\" now can fly until %s!");
        // Invsee
        this.add(getMessageTranslationKey("cantOpenSelfInv"), "You shouldn't open your own inventory in this way!");
        this.add(getTextTranslationKey("playerInv"), "Player \"%s\"'s Inventory");
        // Trashcan
        this.add(getTextTranslationKey("trashcan"), "Trashcan");
        this.add(getTextTranslationKey("clear"), "Clear");
        this.add(getTextTranslationKey("trashcanTitle"), "Clear in %d seconds!");
        // Rank
        this.add(getTextTranslationKey("leaderboard"), "Leaderboard");
        this.add(getTextTranslationKey("deathButton"), "Death");
        this.add(getTextTranslationKey("deathTitle"), "Death Rank");
        this.add(getTextTranslationKey("deaths"), "%s (%d Times)");
        this.add(getTextTranslationKey("timePlayedButton"), "Time Played");
        this.add(getTextTranslationKey("timePlayedTitle"), "Time Played Rank");
        this.add(getTextTranslationKey("timePlayed"), "%s (%dH, %dMin)");
        this.add(getTextTranslationKey("mobsKilledButton"), "Mobs Killed");
        this.add(getTextTranslationKey("mobsKilledTitle"), "Mobs Killed Rank");
        this.add(getTextTranslationKey("mobsKilled"), "%s (%d Mobs)");
        this.add(getTextTranslationKey("distanceWalkedButton"), "Meters Walked");
        this.add(getTextTranslationKey("distanceWalkedTitle"), "Distance Walked Rank");
        this.add(getTextTranslationKey("distanceWalked"), "%s (%d M)");
        this.add(getTextTranslationKey("blocksBrokeButton"), "Blocks Broke");
        this.add(getTextTranslationKey("blocksBrokeTitle"), "Blocks Broke Rank");
        this.add(getTextTranslationKey("blocksBroke"), "%s (%d Blocks)");
        // scessential getRegistryName
        this.add(getMessageTranslationKey("clickToCopy"), "Click to copy to clipboard!");
        this.add(getMessageTranslationKey("nearbyNoLiving"), "No nearby living entities found!");
    }

    private void addServicesTranslations() {
        // Entity Cleaner
        this.add(getMessageTranslationKey("cleanupItemCountdown"), "Item entities will be freed after %d seconds!");
        this.add(getMessageTranslationKey("cleanupMobCountdown"), "Mob entities will be freed after %d seconds!");
        this.add(getMessageTranslationKey("itemCleanupComplete"), "%d item entities freed.");
        this.add(getMessageTranslationKey("mobCleanupComplete"), "%d mob entities freed.");

    }

    public static String getMessageTranslationKey(String afterModId) {
        return TextUtils.getTranslationKey("message", afterModId);
    }

    public static String getTextTranslationKey(String afterModId) {
        return TextUtils.getTranslationKey("text", afterModId);
    }

}
