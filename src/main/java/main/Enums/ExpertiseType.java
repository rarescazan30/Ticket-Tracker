package main.Enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration defining technical expertise types and their compatible developer areas
 */
public enum ExpertiseType {
    FRONTEND(List.of(ExpertiseAreaType.FRONTEND, ExpertiseAreaType.DESIGN)),
    BACKEND(List.of(ExpertiseAreaType.BACKEND)),
    DEVOPS(List.of(ExpertiseAreaType.DEVOPS)),
    DESIGN(List.of(ExpertiseAreaType.DESIGN, ExpertiseAreaType.FRONTEND)),
    DB(List.of(ExpertiseAreaType.DB, ExpertiseAreaType.BACKEND));

    private final List<ExpertiseAreaType> compatibleDevAreas;

    ExpertiseType(final List<ExpertiseAreaType> compatibleDevAreas) {
        this.compatibleDevAreas = compatibleDevAreas;
    }

    /**
     * Returns a list of area names compatible with this expertise type including FULLSTACK
     */
    public List<String> getCompatibleAreas() {
        List<String> areaNames = new ArrayList<>();
        // FULLSTACK is always allowed for every type
        areaNames.add(ExpertiseAreaType.FULLSTACK.name());

        for (ExpertiseAreaType area : compatibleDevAreas) {
            areaNames.add(area.name());
        }
        return areaNames;
    }
}
