package detectors;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import modules.Environment;
import ws3dproxy.model.Thing;

/**
 *
 * @author ia941
 */
public class BrickDetector extends BasicDetectionAlgorithm {

    private static final Logger logger = Logger.getLogger(BrickDetector.class.getCanonicalName());
    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();

    @Override
    public void init() {
        super.init();
        detectorParams.put("mode", "brick");
    }

    @Override
    public double detect() {
        Thing brick = (Thing) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (brick != null) {
            //logger.warning("BRICK");
            activation = 1.0;
        }
        return activation;
    }
}
