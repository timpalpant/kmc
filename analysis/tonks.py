import math, numpy

def heaviside(x):
    if isinstance(x,int) or isinstance(x,float):
        return 1 if x >= 0 else 0
    else:
        return numpy.array(x>=0, dtype=x.dtype)

def factorial(N):
    if not isinstance(N,int):
        N[N<=0] = 1
        
    if numpy.all(N<=1):
        if isinstance(N,int):
            return 1
        else:
            return N
    else:
        b = N-1
        if not isinstance(b,int):
            b[b<=0] = 1
        return N * factorial(b)

def Q(L,N):
    '''
    Canonical partition function for N rods of unit length on a line segment of length L
    '''
    return heaviside(L-N) * (L-N)**N / numpy.array(factorial(N), dtype=float)

def Z(L,u,beta=1):
    '''
    Grand canonical partition function for rods of unit length on 
    a line segment of length L in a bath with chemical potential u
    '''
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(Nmax(L)+1)
        return numpy.sum(numpy.exp(beta*N*u) * Q(L,N))
    else:
        z = numpy.zeros_like(L, dtype=float)
        for i,l in enumerate(L):
            z[i] = Z(l,u,beta)
        return z

def Pn(L,u,N,beta=1):
    '''
    Probability of finding N particles on a line segment of length L
    in a bath with chemical potential u
    '''
    return numpy.exp(beta*N*u) * Q(L,N) / Z(L,u,beta)

def Nmax(L):
    '''
    The maximum number of particles that will fit on a line
    segment of length L
    '''
    return numpy.floor(L)

def Nmean(L,u,beta=1):
    '''
    Mean number of particles on a line segment of length L
    in a bath with chemical potential u
    '''
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(Nmax(L)+1)
        p = Pn(L,u,N,beta)
        return numpy.sum(N*p)
    else:
        nm = numpy.zeros_like(L, dtype=float)
        for i,l in enumerate(L):
            nm[i] = Nmean(l,u,beta)
        return nm

def density(L,u,beta=1):
    return Nmean(L,u,beta) / L

def r1(L,u,x,beta=1):
    '''
    Probability of finding a particle at x (0.5 <= x <= L-0.5)
    on a line segment of length L at chemical potential u
    (one particle distribution function)
    '''
    return numpy.exp(beta*u) * Z(x-0.5,u,beta) * Z(L-x-0.5,u,beta) / Z(L,u,beta)

def r2(L,u,x1,x2,beta=1):
    '''
    Probability of finding a particle at x2 (x1 <= x2 <= L-0.5),
    given that there is a particle at x1, on a line segment of length L
    at chemical potential u
    (two particle distribution function)
    '''
    return numpy.exp(2*beta*u) * Z(x1-0.5,u,beta) * Z(x2-x1-1,u,beta) * Z(L-x2-0.5,u,beta) / Z(L,u,beta)

def Prn(L,N,r):
    '''
    Probability of finding the nearest neighbor of a particle
    within r, on a line segment of length L with N particles of unit width
    '''
    rho = N / L
    return 1 - numpy.exp(2*rho*(r-1)/(rho-1))

def rmean(L,N):
    '''
    Mean distance to the nearest neighbor if there are N particles
    on a line segment of length L
    '''
    return (1+L/N) / 2

def Pr(L,u,r,beta=1):
    '''
    Probability of finding the nearest neighbor of a particle
    within r, on a line segment of length L with chemical potential u
    '''
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(Nmax(L)+1)
        return numpy.sum(Prn(L,N,r)*Pn(L,u,N,beta))
    else:
        pr = numpy.zeros_like(L, dtype=float)
        for i,l in enumerate(L):
            pr[i] = Pr(l,u,r,beta)
        return pr

def hn(L,N,r,beta=1):
    '''
    Probability of finding a hole of size r
    i.e. the probability that there is not a particle within r+1
    for N particles on line segment of length L
    '''
    return 1 - Prn(L,N,r+1)

def h(L,u,r,beta=1):
    '''
    Probability of finding a hole of size r
    i.e. the probability that there is not a particle within r+1
    for a line segment of length L with chemical potential u
    '''
    return 1 - Pr(L,u,r+1,beta)

def entropy(v):
    '''
    Entropy of a vector
    '''
    p = v / numpy.sum(v)
    return -numpy.sum(p*numpy.log2(p))

def bistability(L,u,beta=1):
    '''
    Bistability of an array of length L at chemical potential u.
    The entropy of the probability distribution for number of particles.
    '''
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(Nmax(L)+1)
        return entropy(Pn(L,u,N,beta))
    else:
        B = numpy.zeros_like(L)
        for i,l in enumerate(L):
            B[i] = bistability(l,u,beta)
        return B