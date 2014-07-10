/*
	Processes enronout
	Prints to standard output
	Use topnodes > out to put output into a file

*/

#include <fstream>
#include <string>
#include <sstream>

#include <cstdlib>
#include <iostream>     // std::cout
#include <algorithm>    // std::sort
#include <vector>       // std::vector

#define NUM_NODES 10 // Number of nodes to extract

using namespace std;

struct Node{
	int node, count;
	Node(int myNode, int myCount) {
		node = myNode;
		count = myCount;
	}
};
vector <Node> enron;

bool updateNode(vector<Node> & v, int node1) {
	bool found = false;
	for (vector<Node>::iterator it = v.begin() ; it != v.end(); ++it)
	{
		if( (*it).node == node1 ) {
			(*it).count++;
			found = true;
		}
	}
	if( !found )
	{
		Node* new_node = new Node(node1, 1);
		v.push_back(*new_node);
	}
}

bool nodeCompare( Node n1, Node n2 ) {
	return n1.count > n2.count;
}

void sortVector( vector<Node> & v )
{
	sort(v.begin(), v.end(), nodeCompare);
}

void traverseVector( vector<Node> & v )
{
	vector<Node>::iterator it = v.begin();
	for(int i = 0; i < NUM_NODES; i++) {
		if( it != v.end() ){
			cout << (*it).node << "\t" << (*it).count << endl;
		}
		++it;
	}
}


int main()
{
	string triangles = "enronout";
	ifstream file(triangles.c_str());
	string value;
	int nodeTemp;

	while(!file.eof()){
		getline(file, value, ',');
		stringstream ss(value);
		ss >> nodeTemp;
		updateNode( enron, nodeTemp );
		
		getline(file, value, ',');
		stringstream ss2(value);
		ss2 >> nodeTemp;
		updateNode( enron, nodeTemp );
		
		getline(file, value);
		stringstream ss3(value);
		ss3 >> nodeTemp;
		updateNode( enron, nodeTemp );
	}
	sortVector(enron);
	traverseVector(enron);
	return 0;
}