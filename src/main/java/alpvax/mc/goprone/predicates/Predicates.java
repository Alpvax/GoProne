package alpvax.mc.goprone.predicates;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Predicates {
  public static List<PronePredicate<?>> predicates = new ArrayList<>();

  static {
    predicates.add(PronePredicate.builder()
                       .withComment("Allowed while flying?")
                       .build("flying", true)
    );
    predicates.add(PronePredicate.builder()
                       .withComment("Allowed while riding another entity?")
                       .withExceptions(builder -> builder
                             .comment("List of registry names of entities to allow going prone while riding", "e.g. \"minecraft:boat\"")
                             .defineList("exceptions", new ArrayList<>(),
                                 key -> key instanceof String && ForgeRegistries.ENTITIES.containsKey(new ResourceLocation((String)key))
                             )
                       )
                       .build("riding", false)
    );
  }
}
