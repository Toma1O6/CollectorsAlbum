package team.tnt.collectoralbum.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import team.tnt.collectoralbum.client.screen.CardOpenScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenCardScreenPacket extends AbstractNetworkPacket<OpenCardScreenPacket> {

    private final List<ItemStack> drops;

    public OpenCardScreenPacket() {
        this.drops = Collections.emptyList();
    }

    public OpenCardScreenPacket(List<ItemStack> drops) {
        this.drops = drops;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(drops.size());
        for (ItemStack stack : drops) {
            buffer.writeItem(stack);
        }
    }

    @Override
    public OpenCardScreenPacket decode(PacketBuffer buffer) {
        int count = buffer.readInt();
        List<ItemStack> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ItemStack stack = buffer.readItem();
            list.add(stack);
        }
        return new OpenCardScreenPacket(list);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void handlePacket(NetworkEvent.Context context) {
        CardOpenScreen screen = new CardOpenScreen(drops);
        Minecraft.getInstance().setScreen(screen);
    }

    static class Data {

        private final List<ItemStack> drops;

        public Data(List<ItemStack> drops) {
            this.drops = drops;
        }

        public List<ItemStack> drops() {
            return drops;
        }
    }
}
