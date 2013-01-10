#! /usr/bin/env python

import argparse, math, numpy

def main(args):
    print "Making flat lanscape with value = %g" % args.flat
    v = numpy.ones(args.length) * args.flat
    for pos,height,sigma in args.well:
        print "Making well at pos = %s with depth = %s and stdev = %s" % (pos,height,sigma)
        pos = int(pos)
        height = float(height)
        sigma = float(sigma)
        start = max(0, int(pos - 3*sigma))
        end = min(args.length-1, int(pos + 3*sigma))
        for i in xrange(start,end):
            v[i] += height * math.exp(-(i-pos)**2 / sigma**2)
    numpy.savetxt(args.output, v.T)
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('output', help='Output file')
    parser.add_argument('length', type=int, help='Lattice length')
    parser.add_argument('--flat', type=float, metavar='kT',
                        help='Flat height of landscape')
    parser.add_argument('--well', nargs=3, action='append', metavar='X',
                        help='Make well at position, height, variance')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())