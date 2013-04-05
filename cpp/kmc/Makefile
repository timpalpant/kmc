CC=g++
#CFLAGS=$(DESRES_MODULE_CPPFLAGS) -std=c++11 -c -O3 -march=native -mtune=native -fopenmp -funroll-loops -flto -D_GLIBCXX_PARALLEL
CFLAGS=$(DESRES_MODULE_CPPFLAGS) -std=c++11 -c -O3 -march=native -mtune=native
#LDFLAGS=$(DESRES_MODULE_LDFLAGS) -lboost_system -lboost_filesystem -std=c++11 -O3 -march=native -mtune=native -fopenmp -funroll-loops -flto
LDFLAGS=$(DESRES_MODULE_LDFLAGS) -lboost_system -lboost_filesystem -std=c++11 -O3 -march=native -mtune=native
SRCDIR=kmc
SOURCES=distribution.cpp kmc.cpp lattice.cpp main.cpp nobjects.cpp parameters.cpp particle.cpp state.cpp status.cpp trajectory.cpp transition_manager.cpp
BUILDDIR=build
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=kmc

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS)
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@
