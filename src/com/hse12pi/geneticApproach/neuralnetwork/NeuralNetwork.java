package com.hse12pi.geneticApproach.neuralnetwork;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork implements Cloneable{
	
	protected List<Neuron> neurons;
	protected NetworkLinks neuronsLinks = new NetworkLinks();
	protected int activationIterations = 1;

	public NeuralNetwork() {
		// Required by JAXB
	}

	public NeuralNetwork(int numberOfNeurons) {
		this.neurons = new ArrayList<Neuron>(numberOfNeurons);
		for (int i = 0; i < numberOfNeurons; i++) {
			this.neurons.add(new Neuron(Threshold.SIGN, Threshold.SIGN.getDefaultParams()));
		}
	}

	public void setNeuronFunction(int neuronNumber, Threshold function, List<Double> params) {
		if (neuronNumber >= this.neurons.size()) {
			throw new RuntimeException("Neural network has " + this.neurons.size()
					+ " neurons. But there was trying to accsess neuron with index " + neuronNumber);
		}
		this.neurons.get(neuronNumber).setFunctionAndParams(function, params);
	}

	public void addLink(int activatorNeuronNumber, int receiverNeuronNumber, double weight) {
		this.neuronsLinks.addWeight(activatorNeuronNumber, receiverNeuronNumber, weight);
	}

	public void putSignalToNeuron(int neuronIndx, double signalValue) {
		if (neuronIndx < this.neurons.size()) {
			this.neurons.get(neuronIndx).addSignal(signalValue);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public double getAfterActivationSignal(int neuronIndx) {
		if (neuronIndx < this.neurons.size()) {
			return this.neurons.get(neuronIndx).getAfterActivationSignal();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void activate() {
		for (int iter = 0; iter < this.activationIterations; iter++) {

			for (int i = 0; i < this.neurons.size(); i++) {

				Neuron activator = this.neurons.get(i);
				activator.activate();
				double activatorSignal = activator.getAfterActivationSignal();

				for (Integer receiverNum : this.neuronsLinks.getReceivers(i)) {
					if (receiverNum >= this.neurons.size()) {
						throw new RuntimeException("Neural network has " + this.neurons.size()
								+ " neurons. But there was trying to accsess neuron with index " + receiverNum);
					}
					Neuron receiver = this.neurons.get(receiverNum);
					double weight = this.neuronsLinks.getWeight(i, receiverNum);
					receiver.addSignal(activatorSignal * weight);
				}
			}
		}
	}

	public List<Double> getWeightsOfLinks() {
		return this.neuronsLinks.getAllWeights();
	}

	public void setWeightsOfLinks(List<Double> weights) {
		this.neuronsLinks.setAllWeights(weights);
	}

	public List<Neuron> getNeurons() {
		List<Neuron> ret = new ArrayList<Neuron>(this.neurons.size());
		for (Neuron n : this.neurons) {
			ret.add(n.clone());
		}
		return ret;
	}

	public int getNeuronsCount() {
		return this.neurons.size();
	}

	public void setNeurons(List<Neuron> newNeurons) {
		this.neurons = newNeurons;
	}

	public int getActivationIterations() {
		return this.activationIterations;
	}

	public void setActivationIterations(int activationIterations) {
		this.activationIterations = activationIterations;
	}

	public NetworkLinks getNeuronsLinks() {
		return this.neuronsLinks.clone();
	}

	public NeuralNetwork clone() {
		NeuralNetwork clone = new NeuralNetwork(this.neurons.size());
		clone.neuronsLinks = this.neuronsLinks.clone();
		clone.activationIterations = this.activationIterations;
		clone.neurons = new ArrayList<Neuron>(this.neurons.size());
		for (Neuron neuron : this.neurons) {
			clone.neurons.add(neuron.clone());
		}
		return clone;
	}

	public String toString() {
		return "NeuralNetwork [neurons=" + this.neurons + ", links=" + this.neuronsLinks + ", activationIterations=" + this.activationIterations + "]";
	}

	/*public static void marsall(NeuralNetwork nn, OutputStream out) throws Exception {
		// TODO refactoring
		JAXBContext context = JAXBContext.newInstance(NeuralNetwork.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(nn, out);
		out.flush();
	}

	public static NeuralNetwork unmarsall(InputStream in) throws Exception {
		// TODO refactoring
		JAXBContext context = JAXBContext.newInstance(NeuralNetwork.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		NeuralNetwork unmarshalledNn = (NeuralNetwork) unmarshaller.unmarshal(in);
		return unmarshalledNn;
	}
*/
}
