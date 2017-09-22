package preference.scenario;

import java.util.Arrays;
import java.util.Vector;
import java.util.function.IntFunction;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import preference.PreferenceRelation;

/**
 * Used to model a preference profile.
 * Consists of {@link PreferenceRelation}s.
 * 
 * @author Dominik
 *
 */
public class Scenario implements ListModel<String>{
	private Vector<PreferenceRelation> relations;
	private Vector<String> objects;
	private Vector<String> agents;

	//If the user didn't give a name, use the default name
	private IntFunction<String> defaultObject= (k->"Object "+(k+1));
	private IntFunction<String> defaultAgent = (k->"Agent "+(k+1));

	/**
	 * Generates an scenario with n Agents and n Objects
	 * @param n
	 */
	public Scenario(int n) {
		this(n,n); 
	}

	/**
	 * Generates a new scenario.
	 * @param agents
	 * @param objects
	 */
	public Scenario(int agentCount,int objectCount) {
		relations = new Vector<>(agentCount);
		agents = new Vector<>(agentCount);
		for(int i=0;i<agentCount;i++) {
			agents.add("");
			relations.add(new PreferenceRelation(objectCount));
		}

		objects = new Vector<>(objectCount);
		for(int i=0;i<objectCount;i++) {
			objects.add("");
		}
	}

	public Scenario(String[] age, String[] obj, PreferenceRelation[] rel) {
		if(age.length==rel.length) {

			//Sanitize input
			for(int i=0;i<obj.length;i++) {
				obj[i] = sanitize(obj[i]);
			}

			for(int i=0;i<age.length;i++) {
				age[i] = sanitize(age[i]);
				if(rel[i]==null) {
					throw new IllegalStateException("Relation "+i+" must not be null");
				}

				if(rel[i].getRelationSize()!=obj.length) {
					throw new IllegalStateException("Relation "+i+" must have size "+obj.length+" but has size"+
							rel[i].getRelationSize());
				}
			}


			relations = new Vector<PreferenceRelation>(Arrays.asList(rel));
			objects = new Vector<String>(Arrays.asList(obj));
			agents = new Vector<String>(Arrays.asList(age));
		} else {
			throw new IllegalStateException("Scenario must have the same number of agents, and relations.");
		}
	}

	/*================= AGENTS & OBJECTS =================*/

	/**
	 * Adds both an agent with its preference relation and an object.
	 * @param agent
	 * @param object
	 * @param relationProducer Produces a preference Relation, that has a given size
	 */
	public void add(String agent,String object, IntFunction<PreferenceRelation> relationProducer) {
		addObject(object);
		addAgent(agent, relationProducer);
	}
	
	public void add() {
		add("", "", PreferenceRelation::new);	
	}

	/**
	 * Removes a given agent and object
	 * @param agent
	 * @param object
	 */
	public void remove(int agent, int object) {
		//Remove Agent
		removeAgent(agent);
		//Remove Object
		removeObject(object);
	}

	/*================= AGENTS =================*/

	//ADD
	/**
	 * Adds an agent using the relation producer
	 * @param agent
	 * @param relationProducer Produces a preference Relation, that has a given size
	 */
	public void addAgent(String agent, IntFunction<PreferenceRelation> relationProducer) {
		agents.add(sanitize(agent));
		int size = objects.size();
		PreferenceRelation newRel = relationProducer.apply(size);
		if(newRel.getRelationSize()!=getObjectCount()) {
			throw new IllegalStateException("Produced Relation has not the given size");
		}
		relations.add(relationProducer.apply(size));

		updateAgents(getAgentCount(),ListDataEvent.INTERVAL_ADDED);
		notifyListeners(ScenarioUpdateEvent.AGENT_SET_CHANGED);
	}

	/**
	 * Adds an agent using the relation producer
	 * @param relationProducer Produces a preference Relation, that has a given size
	 */
	public void addAgent(IntFunction<PreferenceRelation> relationProducer) {
		addAgent("", relationProducer);
	}

	public void addAgent(String agent, PreferenceRelation relation) {
		addAgent(agent,(n)->relation);
	}

	/**
	 * Adds a new agent to the scenario
	 * @param relation the preference relation of that new agent
	 */
	public void addAgent(PreferenceRelation relation) {
		addAgent("",relation);
	}

	/**
	 * Adds a new agent to the scenario
	 */
	public void addAgent() {
		addAgent(PreferenceRelation::new);
	}

	//REMOVE
	public void removeAgent(int agentIndex) {
		//Remove Agent
		agents.remove(agentIndex);
		relations.remove(agentIndex);
		updateAgents(agentIndex,ListDataEvent.INTERVAL_REMOVED);
		notifyListeners(ScenarioUpdateEvent.AGENT_SET_CHANGED);
	}

	public void removeAgent() {
		int size = relations.size()-1;
		removeAgent(size);
	}

	//GET / SET	
	public int getAgentCount() {
		return this.agents.size();
	}

	public String getAgent(int i) {
		String out = agents.get(i);
		if(out == null || out.isEmpty()) {
			return defaultAgent.apply(i);
		}
		return out;
	}

	public void setAgent(int i,String agent) {
		String newAgent = sanitize(agent);

		if(newAgent.equals(defaultAgent.apply(i))) {
			newAgent = "";
		}
		agents.set(i, newAgent);
		updateAgents(i, ListDataEvent.CONTENTS_CHANGED);
		notifyListeners(ScenarioUpdateEvent.AGENT_NAME_UPDATE);
	}

	/*================= RELATION =================*/
	public PreferenceRelation getAgentRelation(int i) {
		return this.relations.get(i);
	}

	public void setAgentRelation(int i, PreferenceRelation rel, boolean updateTree) {
		if(rel==null) {
			return;
		}

		PreferenceRelation old = getAgentRelation(i);

		if(old.getRelationSize()==rel.getRelationSize()) {
			this.relations.set(i, rel);
			updateAgents(i, ListDataEvent.CONTENTS_CHANGED);
			
			notifyListeners(ScenarioUpdateEvent.PREFERENCE_UPDATE);
		}
	}

	/*================= OBJECTS =================*/

	//ADD
	public void addObject(String object) {
		objects.add(sanitize(object));
		for(PreferenceRelation rel:relations) {
			rel.addElement();
		}
		notifyListeners(ScenarioUpdateEvent.PREFERENCE_UPDATE);
	}

	public void addObject() {
		addObject("");
	}

	//REMOVE
	public void removeObject(int object)  {
		for(PreferenceRelation rel:relations) {
			rel.removeObject(object);
		}
		this.objects.remove(object);
		notifyListeners(ScenarioUpdateEvent.PREFERENCE_UPDATE);
	}


	//GET / SET
	public int getObjectCount() {
		return this.objects.size();
	}

	public String getObject(int i) {
		String out = objects.get(i);
		if(out == null || out.isEmpty()) {
			return defaultObject.apply(i);
		}
		return out;
	}

	public void setObject(int i,String object) {
		objects.set(i, sanitize(object));
		notifyListeners(ScenarioUpdateEvent.OBJECT_NAME_UPDATE);
	}


	/*================= HELPER FUNCTIONS =================*/

	private String sanitize(String str) {
		if(str==null) {
			return "";
		}
		String out = str.replaceAll(",", "").replaceAll("\n", "");
		out = out.trim();
		return out;
	}

	@Override
	public String toString() {
		String out = "";

		String obj = "[";
		for(int i=0;i<getObjectCount();i++) {
			if(i!=0) {
				obj+=",";
			}
			obj+=getObject(i);
		}
		obj+="]";


		String age = "[";
		for(int i=0;i<agents.size();i++) {
			if(i!=0) {
				age+=",";
				out+="\n";
			}
			age+=getAgent(i);
			out+=relations.get(i).toString();
		}

		age+="]";
		out = age+"\n"+obj+"\n"+out;
		return out;
	}

	/**
	 * Similar to toString, but doesn't use the default Names
	 * @return return a String for saving to disc
	 */
	public String getStoreVersion() {
		String out = "";

		String obj = "[";
		for(int i=0;i<getObjectCount();i++) {
			if(i!=0) {
				obj+=",";
			}
			obj+=sanitize(objects.get(i));
		}
		obj+="]";

		String age = "[";
		for(int i=0;i<agents.size();i++) {
			if(i!=0) {
				age+=",";
				out+="\n";
			}

			age+= sanitize(agents.get(i));
			out+=relations.get(i).getStoreVersion();
		}
		age+="]";
		out = age+"\n"+obj+"\n"+out;
		return out;
	}

	/*================= LIST MODEL =================*/
	// Allows direct connection to JList
	private Vector<ListDataListener> listener = new Vector<>();

	@Override
	public int getSize() {
		return getAgentCount()+1;
	}

	@Override
	public String getElementAt(int index) {
		if(index == 0) {
			return "Overview";
		}
		index--;
		String out = getAgent(index)+": "+getAgentRelation(index).toString();
		return out;
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if(l==null) {
			return;
		}
		listener.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listener.remove(l);
	}

	private void updateAgents(int index, int event) {
		ListDataEvent ev = new ListDataEvent(this, event, index, index);

		for(ListDataListener l:this.listener) {
			l.contentsChanged(ev);
		}
	}

	/*================= PREFERENCE UI UPDATE =================*/
	private Vector<ScenarioListener> listeners = new Vector<>();
	
	private void notifyListeners(ScenarioUpdateEvent e) {
		@SuppressWarnings("unchecked")
		Vector<ScenarioListener> tempList = (Vector<ScenarioListener>) listeners.clone();
		//System.out.println("Scenario Update: "+e);
		
		for(ScenarioListener l: tempList) {
			//System.out.println(l);
			l.valueChanged(e);
		}
	}
	
	public void addScenarioListener(ScenarioListener listener) {
		if(listener!=null) {
			listeners.addElement(listener);
		}
	}
	
	public void removeScenarioListener(ScenarioListener listener) {
		listeners.removeElement(listener);
	}
	
	public void resetAgentNames() {
		for(int i=0;i<getAgentCount();i++) {
			setAgent(i, "");
		}
	}

	public void resetObjectNames() {
		for(int i=0;i<getObjectCount();i++) {
			setObject(i, "");
		}
	}

}
