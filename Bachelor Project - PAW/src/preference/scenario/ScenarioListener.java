package preference.scenario;

/**
 * Helper class.
 * Listener that is called if a {@link Scenario} is modified.
 * @author Dominik
 *
 */
public abstract interface ScenarioListener {
	public void valueChanged(ScenarioUpdateEvent e);
}
