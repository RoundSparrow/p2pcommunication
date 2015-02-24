package no.bouvet.p2pcommunication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;

public class P2pCommunicationFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private final int fragmentCount = 2;

    public P2pCommunicationFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
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
        return fragmentCount;
    }
}