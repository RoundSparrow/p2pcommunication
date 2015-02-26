package no.bouvet.p2pcommunication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;

public class P2pCommunicationFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 2;

    public P2pCommunicationFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return DiscoveryAndConnectionFragment.getInstance();
            case 1:
                return CommunicationFragment.getInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    public DiscoveryAndConnectionFragment getDiscoveryAndConnectionFragment() {
        return (DiscoveryAndConnectionFragment) getItem(0);
    }

    public CommunicationFragment getCommunicationFragment() {
        return (CommunicationFragment) getItem(1);
    }
}