/*
	Processed Email-Enron.txt
	Prints to standard output
	Use preproc > out to put output into a file

*/

#include <fstream>
#include <string>
#include <iostream>
#include <sstream>
#include <vector>
#include <cstdlib>

using namespace std;

struct Node{
	int n1, n2;
	Node(int myNode1, int myNode2) {
		n1 = myNode1;
		n2 = myNode2;
	}
};
vector <Node> enron;

bool findNode(vector<Node> & v, int node1, int node2) {
	bool found = false;
	vector<Node>::iterator ptr;
	for (vector<Node>::iterator it = v.begin() ; it != v.end(); ++it)
	{
		if( ((*it).n1 == node1) && ((*it).n2 == node2) ||
			(((*it).n1 == node2) && ((*it).n2 == node1)) ) {
			found = true;
		}
	}
	return found;
}

void insertNode(vector<Node> & v, int node1, int node2) {
	Node* new_node = new Node(node1, node2);
	v.push_back(*new_node);
}

void traverseVector( vector<Node> & v )
{
	for( vector<Node>::iterator it = v.begin(); it != v.end(); ++it)
	{
		cout << (*it).n1 << "\t" << (*it).n2 << endl;
	}
}


int main()
{
	string graph = "Email-Enron.txt";
	ifstream file(graph.c_str());
	string value;
	int node1, node2, tempInt;

	for(int i = 0; i < 4; i++)
		getline(file, value);
		
	while(!file.eof()){
		getline(file, value);
		stringstream ss(value);
		ss >> node1;
		ss >> node2;
		if(!findNode(enron, node1, node2))
		{
			insertNode(enron, node1, node2);
		}
	}
	
	traverseVector(enron);
	return 0;
}