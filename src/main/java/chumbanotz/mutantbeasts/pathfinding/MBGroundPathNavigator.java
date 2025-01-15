package chumbanotz.mutantbeasts.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MBGroundPathNavigator extends PathNavigateGround {
    private boolean shouldAvoidRain;

    public MBGroundPathNavigator(EntityLiving entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder() {
        this.nodeProcessor = new MBWalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    protected void removeSunnyPath() {
        super.removeSunnyPath();
        if (this.shouldAvoidRain && this.world.isRaining()) {
            if (this.world.isRainingAt(new BlockPos(MathHelper.floor(this.entity.posX), (int) ((this.entity.getEntityBoundingBox()).minY + 0.5D), MathHelper.floor(this.entity.posZ))))
                return;
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); i++) {
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
                if (this.world.isRainingAt(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z))) {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    public void onUpdateNavigation() {
        super.onUpdateNavigation();
        if (!this.entity.isImmuneToFire()) if (this.entity.isInLava()) {
            if (this.entity.getPathPriority(PathNodeType.LAVA) < 8.0F)
                this.entity.setPathPriority(PathNodeType.LAVA, 8.0F);
        } else if (this.entity.getPathPriority(PathNodeType.LAVA) > -1.0F) {
            this.entity.setPathPriority(PathNodeType.LAVA, -1.0F);
        }
    }

    public MBGroundPathNavigator setAvoidRain(boolean avoidRain) {
        this.shouldAvoidRain = avoidRain;
        return this;
    }
}
