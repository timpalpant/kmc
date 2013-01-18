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
    return heaviside(L-N) * (L-N)**N / numpy.array(factorial(N), dtype=float)

def Z(L,u,beta=1):
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(numpy.floor(L)+2)
        return numpy.sum(numpy.exp(beta*N*u) * Q(L,N))
    else:
        z = numpy.zeros_like(L, dtype=float)
        for i,l in enumerate(L):
            z[i] = Z(l,u,beta)
        return z

def P(L,u,N,beta=1):
    return numpy.exp(beta*N*u) * Q(L,N) / Z(L,u,beta)

def Nmean(L,u,beta=1):
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(numpy.floor(L)+2)
        p = P(L,u,N,beta)
        return numpy.sum(N*p)
    else:
        nm = numpy.zeros_like(L, dtype=float)
        for i,l in enumerate(L):
            nm[i] = Nmean(l,u,beta)
        return nm

def r1(L,u,x,beta=1):
    return numpy.exp(beta*u) * Z(x-0.5,u,beta) * Z(L-x-0.5,u,beta) / Z(L,u,beta)

def r2(L,u,x1,x2,beta=1):
    return numpy.exp(2*beta*u) * Z(x1-0.5,u,beta) * Z(x2-x1-1,u,beta) * Z(L-x2-0.5,u,beta) / Z(L,u,beta)

def h(L,u,N,beta=1):
    p = N / L
    return (1-p) * numpy.exp(-p/(1-p))
    
def hmean(L,u,beta=1):
    if isinstance(L,int) or isinstance(L,float):
        N = numpy.arange(numpy.floor(L+2))
        return numpy.sum(h(L,u,N)*P(L,u,N))
    else:
        H = numpy.zeros_like(L)
        for i,l in enumerate(L):
            H[i] = hmean(l,u,beta)
        return H